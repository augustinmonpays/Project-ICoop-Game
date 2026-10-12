package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

import static ch.epfl.cs107.icoop.actor.ICoopPlayer.MOVE_DURATION;

public abstract class Projectiles extends MovableAreaEntity implements Interactor, Unstoppable {

    private double speed;
    private int maxDistance;
    private Orientation orientation;

    public Projectiles(Area area, Orientation orientation, DiscreteCoordinates positionDiscreteCoordinates, double speed, int maxDistance) {
        super(area, orientation, positionDiscreteCoordinates);
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.orientation = orientation;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * On supprime le projectile s'il rencontre un obstacle
     * Et on réduit la distance qu'il peut parcourir, et l'arrête lorsque cette distance est atteinte.
     * @param delta elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        move((int) (MOVE_DURATION / speed));
        if (!getOwnerArea().canLeaveAreaCells(this, List.of(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))){
            getOwnerArea().unregisterActor(this);
        }
        maxDistance--;
        if (maxDistance <= 0) {
            stopProjectile();
        }
    }

    public void stopProjectile() {
        getOwnerArea().unregisterActor(this);
    }
}