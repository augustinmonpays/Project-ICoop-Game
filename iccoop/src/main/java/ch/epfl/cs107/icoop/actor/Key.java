package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Key extends ElementalItem implements Interactable {
    private Sprite keySprite;

    /**
     * la clée qui est dessinnée dépend de l'élément avec lequel elle est créée
     * @param area
     * @param orientation
     * @param position
     * @param element
     */
    public Key(Area area, Orientation orientation, DiscreteCoordinates position, String element) {
        super(area, orientation, position, element);
        if(element.equals("fire")){
            keySprite = new Sprite("icoop/key_red", 0.6f, 0.6f, this);
        }
        else{
            keySprite = new Sprite("icoop/key_blue", 0.6f, 0.6f, this);
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

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void draw(Canvas canvas){
        keySprite.draw(canvas);
    }

    @Override
    public void update(float delta){
       if (isCollected()){
            getOwnerArea().unregisterActor(this);
        }
    }
}
