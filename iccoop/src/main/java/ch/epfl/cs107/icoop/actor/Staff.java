package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Staff extends ElementalItem {
    private String spriteName;
    private Animation staffAnimation;
    public final int ANIMATION_DURATION = 32;
    private boolean isCollected = false;

    public Staff(Area area, Orientation orientation, DiscreteCoordinates position, String element, String name){
        super(area, orientation, position, element);
        spriteName = name;
        staffAnimation = new Animation(spriteName , 8, 2, 2, this , 32, 32, new Vector(-0.5f, 0), ANIMATION_DURATION/8, true);
    }

    public void draw(Canvas canvas) {
        staffAnimation.draw(canvas);
    }

    public void update(float deltaTime){
        staffAnimation.update(deltaTime);
        if (isCollected){
            getOwnerArea().unregisterActor(this);
        }
        super.update(deltaTime);

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

    public void collectStaff(){
        isCollected = true;
    }


}