package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.icoop.actor.ICoopPlayer.MOVE_DURATION;
import static ch.epfl.cs107.play.math.Orientation.*;
import static ch.epfl.cs107.play.math.Orientation.LEFT;

public class logMonster extends Foe {
    private Animation sleepingAnimation;
    private OrientedAnimation awakeAnimation;
    private OrientedAnimation walkingAnimation;
    private final Orientation[] orders = {DOWN, UP, RIGHT, LEFT};
    private final Vector anchor = new Vector(0, 0);
    private Behavior behavior = Behavior.SLEEPING;
    private logMonsterInteractionHandler handler;
    private double speed = 1;
    private int wakingUpTimer = 30;
    private int attentionTimer = 300;
    private int reloadTimer = 20;
    private int innactionDuration;

    /**
     * Les LogMonsters changent d'animation selon leur état.
     * Ils peuvent être soit endormis, soit en train de se réveiller, soit réveillés
     * Ce qui aura une incidence sur leur comportement
     */

    public logMonster(Area owner, Orientation orientation, DiscreteCoordinates coordinates, List<AttackType> vulnerableAttacks, int maxLife) {
        super(owner, orientation, coordinates, vulnerableAttacks, maxLife);
        handler = new logMonsterInteractionHandler();
        sleepingAnimation = new Animation(behavior.getAnimationName(), 1, 2, 2, this, 32, 32, 12, true );
        awakeAnimation = new OrientedAnimation("icoop/logMonster", 4, this, anchor, orders, 1, 2, 2, 32, 32, true);
        walkingAnimation = new OrientedAnimation("icoop/logMonster", 4, this, anchor, orders, 1, 2, 2, 32, 32, false);
    }

    /**
     *Les monstres possèdent différents timer, qui permettent de l'empêcher de répéter
     * certaines actions. Par exemple les logMonsters ont un temps de rechargement
     * entre deux tirs de flèche pour ne pas qu'ils tirent trop souvent
     */

    public void update(float deltaTime){
        if (innactionDuration > 0){
            innactionDuration --;
        }
        if (behavior == Behavior.SLEEPING ){
            sleepingAnimation.update(deltaTime);
        }

        else if (behavior == Behavior.SHOOTING_ARROW){
            reloadTimer --;
            if (reloadTimer <= 0){
                behavior = Behavior.ATTACKING;
                reloadTimer = 20;
            }
        }

        /**
         * réduit le timer représentant le temps dont il a besoin pour se réveiller
         * complètement, et lorsqu'il atteint 0, le monstre voudra attaquer le player
         */

        else if (behavior == Behavior.WAKING_UP){
            wakingUpTimer --;
            attentionTimer = 300;
            wakingUp();
            if (wakingUpTimer <= 0){
                behavior = Behavior.ATTACKING;
            }
        }

        /**
         * s'ile st réveillé, il se déplace aléatoirement et son timer de temps passé
         * en étant réveillé s'active, faisant qu'il se rendormira au bout d'un certain temps
         */
        else {
            randomMovement();
            attentionTimer --;
            if (!isDisplacementOccurs()){
                awakeAnimation.update(deltaTime);
            }
            else {
                walkingAnimation.update(deltaTime);
            }
            if (attentionTimer <= 0){
                goToSleep();
            }
            if (innactionDuration <= 0){
                randomMovement();
            }
        }
        super.update(deltaTime);
    }

    /**
     * dessine le sprite selon son état
     */

    public void draw(Canvas canvas){
        if (isDead() == Logic.TRUE){
            super.draw(canvas);
        }
        else if (behavior == Behavior.SLEEPING){
            sleepingAnimation.draw(canvas);
        }
        else if (behavior == Behavior.WAKING_UP){
            sleepingAnimation.draw(canvas);
        }
        else {
            if (isDisplacementOccurs()){
                walkingAnimation.draw(canvas);
            }
            else {
                awakeAnimation.draw(canvas);
            }

        }
    }

    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * le met dans un état de sommeil
     */
    public void goToSleep(){
        behavior = Behavior.SLEEPING;
        sleepingAnimation = new Animation(behavior.getAnimationName(), 1, 2, 2, this, 32, 32, 12, true );
    }

    /**
     * le met dans l'état ou il est en train de se réveiller
     */
    public void wakingUp(){
        sleepingAnimation = new Animation(behavior.getAnimationName() , 1, 2, 2, this, 32, 32, 12, false );
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        for (int i = 1; i <= 8; i++) {
            mainCell = mainCell.jump(getOrientation().toVector());
            cells.add(mainCell);
        }
        return cells;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    /**
     * se déplace aléatoirement de manière similaire aux artificiers
     */
    private void randomMovement(){
        if(!isDisplacementOccurs()){
            resetMotion();
            double randomDouble = RandomGenerator.getInstance().nextDouble();
            if (randomDouble < 0.4) {
                Orientation setNewOrientation = Orientation.values()[RandomGenerator.getInstance().nextInt(4)];
                orientate(setNewOrientation);
            }
            move((int) (ANIMATION_DURATION/speed));
            innactionDuration++;
        }
    }

    /**
     * créer un flèche devant le monstre lorsque le joueur passe devant et qu'il
     * est en mode attaque
     */
    public void shootArrow(){
        behavior = Behavior.SHOOTING_ARROW;
        reloadTimer = 20;
        List<DiscreteCoordinates> playerFOV = getFieldOfViewCells();
        List<DiscreteCoordinates> firstCellInFOV = new ArrayList<>();
        firstCellInFOV.add(playerFOV.get(0));
        getOwnerArea().registerActor(new Arrow(getOwnerArea(), getOrientation(),firstCellInFOV.get(0), 2, 45 ));
    }

    private enum Behavior {
        SLEEPING("icoop/logMonster.sleeping"),
        WAKING_UP("icoop/logMonster.wakingUp"),
        ATTACKING("icoop/logMonster"),
        SHOOTING_ARROW("icoop/logMonster");

        private String animationName;

        Behavior(String animationName) {
            this.animationName = animationName;
        }

        public String getAnimationName() {
            return animationName;
        }

    }

    private class logMonsterInteractionHandler implements ICoopInteractionVisitor {

        public void interactWith(ICoopPlayer player, boolean b){
            if (behavior == Behavior.SLEEPING){
                behavior = Behavior.WAKING_UP;
            }
            if (behavior == Behavior.ATTACKING){
                shootArrow();
            }
        }
    }

}
