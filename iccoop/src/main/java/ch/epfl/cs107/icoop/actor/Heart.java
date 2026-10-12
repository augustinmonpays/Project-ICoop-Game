package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Heart extends ICoopCollectable implements ICoopInteractionVisitor {

    private Animation heartAnimation;
    private static final int ANIMATION_DURATION = 24;
    private final int amountOfHeal = 1;

    public Heart(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected){
        super(area, orientation, position, isCollected);
        heartAnimation = new Animation("icoop/heart", 4, 1, 1, this, 16, 16, ANIMATION_DURATION/24, true);
    }

    @Override
    public void update(float deltaTime){
        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        heartAnimation.draw(canvas);
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public int getAmountOfHeal(){
        return amountOfHeal;
    }

    public void collectHeart(){
        getOwnerArea().unregisterActor(this);
    }

}
