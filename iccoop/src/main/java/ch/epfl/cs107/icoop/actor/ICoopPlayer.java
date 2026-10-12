package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.Extensions.*;
import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static ch.epfl.cs107.play.math.Orientation.*;

/**
 * A ICoopPlayer is a player for the ICoop game.
 */
public class ICoopPlayer extends MovableAreaEntity implements ElementalEntity, Interactor, ICoopInteractionVisitor {
    public final static int MOVE_DURATION = 8;
    public final static int SPRINT_DURATION = 4;
    private final OrientedAnimation orientedAnimation;
    private Health health;
    private String element;
    private KeyBindings.PlayerKeyBindings key;
    private final ICoopPlayerInteractionHandler handler;
    private boolean passingADoor;
    private String area = getOwnerArea().getTitle();
    private ArrayList<DiscreteCoordinates> coordinatesOfTheDoor;
    private final int MAX_LIFE = 5;
    private int immunity;
    private Logic takeFireDamage = Logic.TRUE;
    private Logic takeWaterDamage = Logic.TRUE;
    private Logic leavingPressurePlate = Logic.FALSE;
    private ICoopPlayerStatusGUI gui;
    private ICoopInventory inventory;
    private ICoopItem currentItem;
    private ArrayList<ICoopItem> listOfItem = new ArrayList<>();
    private Logic isStunned = Logic.FALSE;
    private Logic isUnStunned = Logic.FALSE;
    private Logic hideInventory = Logic.FALSE; // parfois c'est mieux de ne pas dessiner l'inventaire, par exemple lors du stunning effect
    private Logic isQuaked = Logic.FALSE;
    private Logic isUnQuaked = Logic.FALSE;
    private int quakeDuration;
    private State state = State.NOTHING;
    final Vector playerAnchor = new Vector(0, 0);
    final Vector swordAnchor = new Vector(-0.5f, 0);
    final Vector staffAnchor = new Vector(-0.5f, -0.2f);
    final Orientation[] playerOrders = {DOWN, RIGHT, UP, LEFT};
    final Orientation[] weaponOrders = {DOWN, UP, RIGHT, LEFT};
    private OrientedAnimation swordAnimation;
    private OrientedAnimation staffAnimation;
    private SprintBar sprintBar;
    private final int MAX_SPRINT = 200;

    public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName, String element, KeyBindings.PlayerKeyBindings key) {

        super(owner, orientation, coordinates);
        this.key = key;
        this.element = element;
        this.handler = new ICoopPlayerInteractionHandler();
        orientedAnimation = new OrientedAnimation(spriteName, 4, this,
                playerAnchor, playerOrders, 4, 1, 2, 16, 32,
                true);
        /**
         * on créée une barre de vie en dessous de la sprint bar
         */
        health = new Health(this, Transform.I.translated(0, 1.75f), MAX_LIFE, true);
        sprintBar = new SprintBar(this, Transform.I.translated(0, 2.0f), MAX_SPRINT);

        this.inventory = new ICoopInventory();
        inventory.addPocketItem(ICoopItem.SWORD, 1);
        inventory.addPocketItem(ICoopItem.EXPLOSIVE, 1);

        /**
         * Construit l'apparence de l'inventaire ainsi que les animations d'attaque selon le joueur
         */
        if (element.equals("fire")) {
            gui = new ICoopPlayerStatusGUI(this, false);
            swordAnimation = new OrientedAnimation("icoop/player.sword", 2, this, swordAnchor, weaponOrders, 4, 2, 2, 32, 32);
            staffAnimation = new OrientedAnimation("icoop/player.staff_fire", 2, this, staffAnchor, weaponOrders, 4, 2, 2, 32, 32);
        }
        if (element.equals("water")) {
            gui = new ICoopPlayerStatusGUI(this, true);
            swordAnimation = new OrientedAnimation("icoop/player2.sword", 2, this, swordAnchor, weaponOrders, 4, 2, 2, 32, 32);
            staffAnimation = new OrientedAnimation("icoop/player2.staff_water", 2, this, staffAnchor, weaponOrders, 4, 2, 2, 32, 32);
        }

        listOfItem.add(ICoopItem.SWORD);
        listOfItem.add(ICoopItem.FIRE_KEY);
        listOfItem.add(ICoopItem.WATER_KEY);
        listOfItem.add(ICoopItem.FIRE_STAFF);
        listOfItem.add(ICoopItem.WATER_STAFF);
        listOfItem.add(ICoopItem.EXPLOSIVE);

        currentItem = getFirstItem();
    }

    public String element() {
        return element;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        sprintBar.increase(2);

        if (isDisplacementOccurs()) {
            orientedAnimation.update(deltaTime);
        } else {
            orientedAnimation.reset();
        }
        Keyboard keyboard = getOwnerArea().getKeyboard();
        moveIfPressed(Orientation.LEFT, keyboard.get(key.left()));
        moveIfPressed(Orientation.UP, keyboard.get(key.up()));
        moveIfPressed(Orientation.RIGHT, keyboard.get(key.right()));
        moveIfPressed(Orientation.DOWN, keyboard.get(key.down()));
        immunity--;
        /**
         * si le joueur se déplace on vérifie a chaque fois que ça n'est pas ces coordonnées précises
         * qui correspondent a des PressuresPlates. Si c'est le cas il l'a désactive (Dans onLeaving)
         */
        if (isDisplacementOccurs()) {
            onLeaving(Collections.singletonList(new DiscreteCoordinates(5, 7)));
            onLeaving(Collections.singletonList(new DiscreteCoordinates(5, 10)));
        }
/**
 * si le joueur ne fait rien(attaque pas, ne pause pas de bombe) il peut switch item ou use item.
 */
        if (keyboard.get(key.switchItem()).isPressed() && state == State.NOTHING) {
            switchItem();
        }
        if (keyboard.get(key.useItem()).isPressed() && state == State.NOTHING) {
            useItem();
        }

        /**
         * anime le personnage selon son état (s'il attaque ou autre)
         * */
        else if (state == State.ATTACKING_SWORD){
            if (swordAnimation.isCompleted()){
                state = State.NOTHING;
                swordAnimation.reset();
            }
            else {
                swordAnimation.update(deltaTime);
            }
        }
        else if (state == State.ATTACKING_STAFF){
            if (staffAnimation.isCompleted()){
                state = State.NOTHING;
                staffAnimation.reset();
            }
            else {
                staffAnimation.update(deltaTime);
            }
        }

        /**
         * lorsque la durée est écoulée on désactive l'effet de quake
         */
        if (quakeDuration > 0){
            quakeDuration--;
            if(quakeDuration == 0){
                unquake();
            }
        }
    }

    /**
     * si il quitte des coordonées que l'on ca choisir, il va activer le Logic leavingPressurePlate.
     * Et lorsqu'il va intéragir avec une PressurePlate son action va dépendre de ce Logic( c.f. interactWith (Pressure plate...))
     * @param coordinates left cell coordinates
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        leavingPressurePlate = Logic.TRUE;
    }

    /**
     * Cette méthode est utilisée dans update et vérifie si la touche en paramètre en pressée,
     * si c'est le cas il y a deux choix :
     * - Si la touche de sprint est pressée (CTRL ou SHIFT) le player utilise la méthode move avec une duration
     *    plus petite que son move normal (donc il va plus vite)
     * - Sinon il va a la vitesse normale
     * @param orientation
     * @param b
     */
    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                Keyboard keyboard = getOwnerArea().getKeyboard();

                if(keyboard.get(key.sprint()).isDown() && sprintBar.isOn() && sprintBar.CurrentSprintRemaining()>=50){ //on attend que la valeur revienne a 50 pour faire un sorte de CoolDown
                    move(SPRINT_DURATION);
                    sprintBar.decrease(40);
                }else{
                    move(MOVE_DURATION);
                }
            }
        }
    }

    /**
     * choisi l'animation a attribuer au player selon ce qu'il fait
     */

    @Override
    public void draw(Canvas canvas) {
        if (state == State.ATTACKING_SWORD){
            swordAnimation.draw(canvas);
        }
        else if (state == State.ATTACKING_STAFF){
            staffAnimation.draw(canvas);

        }

        /**
         * fait clignoter le sprite si le personnage prend des dégats
         */
        else {
            if (immunity > 0) {
                if ((immunity % 5) == 0) {
                    orientedAnimation.draw(canvas);
                }
            }
            else {
                orientedAnimation.draw(canvas);
            }
        }
        health.draw(canvas);
        sprintBar.draw(canvas);
        if (hideInventory.isOff()){
        gui.draw(canvas);
        }
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public String getArea() {
        return area;
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    public Health getPlayerHP() {
        return health;
    }

    public ArrayList<DiscreteCoordinates> getDoorCoordinates() {
        return coordinatesOfTheDoor;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    public boolean passingADoor() {
        return passingADoor;
    }

    public void setPassingADoor(boolean b) {
        passingADoor = b;
    }

    /**
     *donne le premier item présent dans l'invantaire
     */

    public ICoopItem getFirstItem() {
        for (ICoopItem item : listOfItem) {
            if (inventory.contains(item)) {
                return item;
            }
        }
        return null;
    }

    public ICoopItem getCurrentItem(){
        return currentItem;
    }

    /**
     * change l'item du joueur pour le prochain présent dans son inventaire
     * selon les items qu'il possède (s'il n'a pas de bombe il passera directement
     * au bâton par exemple)
     */

    public void switchItem() {
        int placeOnList = listOfItem.indexOf(currentItem);
        for (int i = 1; i < listOfItem.size(); i++) {
            int nextPlaceOnList = (placeOnList + i) % listOfItem.size();
            ICoopItem nextItem = listOfItem.get(nextPlaceOnList);
            if (inventory.contains(nextItem)) {
                currentItem = nextItem;
            }
        }
    }

    /**
     * si le personnage n'est pas déjà en train d'effectuer une action, qu'il appuie
     * sur la touche d'action et qu'il possède un item en main, active une méthode
     * permettant au joueur d'utiliser son objet
     */

    public void useItem() {
        if (state == State.NOTHING){
            if (currentItem.equals(ICoopItem.EXPLOSIVE)) {
                state = State.DROPPING_EXPLOSIVE;
                bombAttack();
                state = State.NOTHING;
            }
            else if (currentItem.equals(ICoopItem.SWORD)){
                state = State.ATTACKING_SWORD;
            }
            else if (currentItem.equals(ICoopItem.WATER_STAFF) || currentItem.equals(ICoopItem.FIRE_STAFF)){
                state = State.ATTACKING_STAFF;
                staffAttack();
            }
        }
    }

    /**
     * pose une bombe si la case devant le joueur accepte qu'on pose une bombe dessus
     */

    public void bombAttack(){
        List<DiscreteCoordinates> playerFOV = getFieldOfViewCells();
        List<DiscreteCoordinates> firstCellInFOV = new ArrayList<>();
        firstCellInFOV.add(playerFOV.get(0));
        boolean isCellInteractable = getOwnerArea().canEnterAreaCells(this, firstCellInFOV);
        if (isCellInteractable){
            state = State.DROPPING_EXPLOSIVE;
            inventory.removePocketItem(ICoopItem.EXPLOSIVE, 1);
            getOwnerArea().registerActor(new Explosive(getOwnerArea(), Orientation.DOWN, firstCellInFOV.get(0), 60));
            if (!(inventory.contains(ICoopItem.EXPLOSIVE))) {
                switchItem();
            }
        }
    }

    /**
     * tire une boule de feu/eau devant soit
     */

    public void staffAttack(){
        List<DiscreteCoordinates> playerFOV = getFieldOfViewCells();
        List<DiscreteCoordinates> firstCellInFOV = new ArrayList<>();
        firstCellInFOV.add(playerFOV.get(0));
        Area area = getOwnerArea();
        if (currentItem.equals(ICoopItem.FIRE_STAFF)){
            area.registerActor(new MagicBall(area, getOrientation(), firstCellInFOV.get(0), 3, 45, "fire"));
        }
        else {
            area.registerActor(new MagicBall(area, getOrientation(), firstCellInFOV.get(0), 3, 45, "water"));
        }
    }

    private void setImmunity(int immunity) {
        this.immunity = immunity;
    }

    /**
     * cette méthode est appelée a chaque fois qu'il se fait attaquer :
     * Tout d'abord on vérifie si il n'est pas en période d'immunité
     * Puis on vérifie le type d'attaque et si il peut prendre ce type d'attaque la
     * Enfin prend les dégâts si les conditions sont remplies
     * @param damage
     * @param attackType
     */
    public void attack(int damage, AttackType attackType) {
        if (immunity <= 0) {
            if ((attackType.equals(AttackType.WATER_ATTACK) && takeWaterDamage.isOn())) {
                health.decrease(damage);
                setImmunity(24);
            }
            if ((attackType.equals(AttackType.FIRE_ATTACK) && takeFireDamage.isOn())) {
                health.decrease(damage);
                setImmunity(24);
            }
            if ((attackType.equals(AttackType.PHYSICAL_ATTACK))) {
                health.decrease(damage);
                setImmunity(24);
            }
        }
    }

    /**toutes ces méthodes concernent notre extension des potions
     *ici ce sont celle pour la Stunningpotion.
     * Lorsqu'il est stun le test qui est dans ICoop qui demande si il est stun a chaque update sera
     * valide, et donc l'effet sera mis en place depuis ICoop.
     */
    public void stun(){
        isStunned = Logic.TRUE;
        isUnStunned = Logic.FALSE;
    }
    public boolean isStunned(){return isStunned.isOn();}

    public boolean isUnStunned(){return isUnStunned.isOn();}

    public void unstun(){
        isStunned = Logic.FALSE;
        isUnStunned = Logic.TRUE;
    }

    /**
     * ici ce sont celles de la QuakePotion.
     * C'est la même chose que tout ce qui concerne le stun, sauf que l'effet visuel
     * est géré différemment dans ICoop
     */
    public void quake(){
        isQuaked = Logic.TRUE;
        isUnQuaked = Logic.FALSE;
    }
    public boolean isQuaked(){return isQuaked.isOn();}

    public boolean isUnQuaked(){return isUnQuaked.isOn();}

    public void unquake(){
        isQuaked = Logic.FALSE;
        isUnQuaked = Logic.TRUE;
    }
    
    public void fastQuake(){
        quake();
        quakeDuration = 5;
    }

    public void longQuake() {
        quake();
        quakeDuration = 40;
    }

    public void hideInventory(){hideInventory = Logic.TRUE;}

    public void drawInventory(){hideInventory = Logic.FALSE;}


    private class ICoopPlayerInteractionHandler implements ICoopInteractionVisitor {

        /**
         *récupère la position à laquelle il doit arriver après avoir passé la porte
         *et active le changement d'aire dans ICoop
         */
        @Override
        public void interactWith(Door door, boolean isCellInteraction) {
            if (isCellInteraction && door.isOn()) {
                coordinatesOfTheDoor = door.getSpawnCoordinates();
                passingADoor = true;
                area = door.getArea();
            }
        }

        /**
         *agit comme une porte normale mais avec un dialogue
         */
        @Override
        public void interactWith(SanctumDoor door, boolean isCellInteraction) {
            if (isCellInteraction && door.isOn()) {
                door.publishGoIn();
                coordinatesOfTheDoor = door.getSpawnCoordinates();
                passingADoor = true;
                area = door.getArea();
            }
        }

        @Override
        public void interactWith(Teleporter teleporter, boolean isTeleporterInteraction) {
            if (isTeleporterInteraction) {
                coordinatesOfTheDoor = teleporter.getSpawnCoordinates();
                passingADoor = true;
                area = teleporter.getArea();
            }
        }

        /**
         * Interaction a distance : on vérifie que la touche de useItem du player est en train
         *      d'être appuyée => si oui on active la bombe
         * Interaction de contact : Si la bombe n'est pas activée elle est collectée
         * @param explosif
         * @param isBombInteraction
         */
        @Override
        public void interactWith(Explosive explosif, boolean isBombInteraction) {
            if (!isBombInteraction) {
                Keyboard keyboard = getOwnerArea().getKeyboard();
                if ((keyboard.get(key.useItem()).isDown())) {
                    explosif.activate();
                }

            } else if (!(explosif.isActivated())) {
                explosif.collect();
                inventory.addPocketItem(ICoopItem.EXPLOSIVE, 1);
            }

        }

        /**
         * C'est ici que l'immunité a certains types d'attaque est géré
         * En fonction de l'élément de l'Orb on acquiert l'immunité correspondante
         * @param orb
         * @param isOrbInteraction
         */
        @Override
        public void interactWith(Orb orb, boolean isOrbInteraction) {
            if (isOrbInteraction) {
                orb.collect(element());

                if (orb.element().equals("water")) {
                    takeWaterDamage = Logic.FALSE;
                } else {
                    takeFireDamage = Logic.FALSE;
                }
            }
        }

        /**
         * lorsqu'on est pas déja au niveau de santé maximal, on récupère le coeur et ça nous donnes
         * des hp en plus
         * @param heart
         * @param isHeartInteraction
         */
        @Override
        public void interactWith(Heart heart, boolean isHeartInteraction) {
            if (isHeartInteraction && health.getCurrentHealth() < health.getMaxHealth()) {
                health.increase(heart.getAmountOfHeal());
                heart.collectHeart();
            }
        }

        /**
         * Interaction de contact : on active la pressure Plate puis au moment ou on
         * quitte une celle contenant une PressurePlate, on la désactive (c.f. la méthode
         * onLeaving et son utilisation dans update)
         * @param pressurePlate
         * @param isPressurePlateInteraction
         */
        @Override
        public void interactWith(PressurePlate pressurePlate, boolean isPressurePlateInteraction) {
            if (isPressurePlateInteraction) {
                pressurePlate.activatePressurePlate();
                if (leavingPressurePlate.isOn()) {
                    pressurePlate.deactivatePressurePlate();
                    leavingPressurePlate = Logic.FALSE;
                }
            }
        }

        /**
         *collecte les potions si le personnage marche dessus
         */

        @Override
        public void interactWith(StunningPotion stunningPotion, boolean isStunningPotionInteraction) {
            stunningPotion.collect();
        }

        @Override
        public void interactWith(HealingPotion healingPotion, boolean isHealingpotionInteraction){
            healingPotion.collect();
        }

        @Override
        public void interactWith(Bier bier, boolean isBierInteraction) {
            bier.collect();
        }

        @Override
        public void interactWith(QuakePotion quakePotion, boolean isQuakePotionInteraction) {
            quakePotion.collect();
        }

        /**
         * on peut que prendre la porte finale quand elle est Ouverte.
         * On a des dialogues en fonction de son état
         * @param finalDoor
         * @param isFinalDoorInteraction
         */
        @Override
        public void interactWith(FinalDoor finalDoor, boolean isFinalDoorInteraction) {
            if(finalDoor.isOpened()){
                finalDoor.publishGoIn();
                coordinatesOfTheDoor = finalDoor.getSpawnCoordinates();
                passingADoor = true;
                area = finalDoor.getArea();
            }
            else {
                finalDoor.publishStayOut();
            }
        }

        /**
         *récupère le bâton s'il est du même élément que le personnage
         * sinon, empêche le personnage d'aller sur la case
         */

        @Override
        public void interactWith(Staff staff, boolean isStaffInteraction){
            if (isStaffInteraction && staff.element().equals("fire")){
                inventory.addPocketItem(ICoopItem.FIRE_STAFF, 1);
                staff.collectStaff();
                staff.collect();
            }
            else if (isStaffInteraction){
                inventory.addPocketItem(ICoopItem.WATER_STAFF, 1);
                staff.collectStaff();
                staff.collect();
            }
        }

        /**
         * on peut que prendre les clés du même élément
         */
        @Override
        public void interactWith(Key key, boolean isKeyInteraction) {
            if(element().equals("fire")) {
                inventory.addPocketItem(ICoopItem.FIRE_KEY, 1);
                key.collect();
            }
            else {
                inventory.addPocketItem(ICoopItem.WATER_KEY, 1);
                key.collect();
            }
        }

        @Override
        public void interactWith(Lever lever, boolean isLeverInteraction) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            if(keyboard.get(key.useItem()).isPressed()){
                lever.toggleLever();
            }
        }

        /**
         * inflige des dégats si le player est en train d'attaqué et
         * qu'un adversaire se trouve devant lui
         */

        public void interactWith(Foe foe, boolean isFoeInteraction){
            if (state == State.ATTACKING_SWORD){
                foe.attack(2, AttackType.PHYSICAL_ATTACK);
            }
        }

        public void interactWith(Chest chest, boolean isChestInteraction){
            Keyboard keyboard = getOwnerArea().getKeyboard();
            if(keyboard.get(key.useItem()).isPressed()){
                chest.openChest();
            }
        }
    }
}
