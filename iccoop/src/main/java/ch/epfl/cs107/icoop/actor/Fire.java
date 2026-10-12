package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Fire extends Projectiles {
    private Animation flameAnimation;
    private FireInteractionHandler handler;

    public Fire(Area area, Orientation orientation, DiscreteCoordinates positionDiscreteCoordinates, double speed, int maxDistance) {
        super(area, orientation, positionDiscreteCoordinates, speed, maxDistance);
        this.handler = new FireInteractionHandler();
        flameAnimation = new Animation("icoop/fire", 7, 1, 1, this, 16, 16, 4, true);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void draw (Canvas canvas) {
        flameAnimation.draw(canvas);
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * son champ de vue son les cells autour de lui
     * @return
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates position = new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y);
        return position.getNeighbours();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class FireInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer player, boolean isPlayerInteraction) {
            player.attack(1,AttackType.FIRE_ATTACK);
            stopProjectile();
        }
        @Override
        public void interactWith(Explosive explosif, boolean isPlayerInteraction) {
            explosif.activate();
            stopProjectile();
        }

    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }


}