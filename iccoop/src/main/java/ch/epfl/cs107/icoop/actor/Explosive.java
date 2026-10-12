package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Explosive extends ICoopCollectable implements Interactor {
    private static final int ANIMATION_DURATION = 24; //Je n'ai pas trouvé la valeur autre part.
    private int timer;
    private Logic timerActivated = Logic.FALSE;
    private Logic exploding = Logic.FALSE;
    private Logic exploded = Logic.FALSE;
    private Animation animatedExplosion;
    private ExplosiveInteractionHandler handler;


    /**
     * on a l'option de mettre un timer ou non, le timer par défaut est donc 60
     * @param owner
     * @param orientation
     * @param coordinates
     * @param timer
     */
    public Explosive(Area owner, Orientation orientation, DiscreteCoordinates coordinates, int timer) {
        super(owner, orientation, coordinates);
        this.timer = timer;
        animatedExplosion = new Animation("icoop/explosive", 2, 1, 1, this, 16, 16,
                ANIMATION_DURATION / 2, true);
        handler = new ExplosiveInteractionHandler();
    }

    public Explosive(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        this(owner, orientation, coordinates, 60);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        /**
         * si le timer est activé, il diminue. Et quand il est égal a 0, on set une nouvelle animation
         * d'explosion
         */
        if (timerActivated.isOn() && exploding.isOff()) {
            timer--;
            animatedExplosion.update(deltaTime);
            if (timer == 0) {
                animatedExplosion = new Animation("icoop/explosion", 7, 1, 1, this, 32, 32,
                        ANIMATION_DURATION / 7, false);
                exploded = Logic.TRUE;
                exploding = Logic.TRUE;
            }
        }
        /**
         * on attend le moment d'avoir fini l'animation pour unregister l'actor
        */
        if (exploding.isOn()) {
            animatedExplosion.update(deltaTime);
            if (animatedExplosion.isCompleted()) {
                getOwnerArea().unregisterActor(this);
                exploding = Logic.FALSE;
                timerActivated = Logic.FALSE;
            }
        }

        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates position = new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y);
        return position.getNeighbours();
    }

    @Override
    public boolean takeCellSpace() {
        return (exploding.isOn());
    }

    @Override
    public boolean isCellInteractable() {
        return exploded.isOff();
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public boolean wantsCellInteraction() {
        return exploding.isOn();
    }

    @Override
    public boolean wantsViewInteraction() {
        return exploding.isOn();
    }


    @Override
    public void draw(Canvas canvas) {
        animatedExplosion.draw(canvas);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    public void activate() {
        this.timerActivated = Logic.TRUE;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
    public boolean isActivated(){
        if (timer != 60){
            return true;
        }
        return false;
    }

    /**
     * Quand une bombe va en activer une autre ça ne sera pas un simple "activate" mais un
     * "chainExplosion" avec un timer a 1 pour que la bombe explose tout de suite pour avoir une
     * réaction en chaine
     */
    public void chainExplosion(){
        this.timerActivated = Logic.TRUE;
        timer = 1;
    }


    private class ExplosiveInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(Rocks rock, boolean b) {
            rock.destroy();
        }

        @Override
        public void interactWith(ICoopPlayer player, boolean b){
            player.attack(2, AttackType.PHYSICAL_ATTACK);
            player.fastQuake();
        }

        @Override
        public void interactWith(Explosive explosive, boolean b){
            explosive.chainExplosion();
        }

        @Override
        public void interactWith(ElementalWall elementalWall, boolean b){
            elementalWall.destroyWall();
        }

        @Override
        public void interactWith(Foe foe, boolean b){
            foe.attack(2, AttackType.PHYSICAL_ATTACK);
        }

    }
}