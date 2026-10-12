package ch.epfl.cs107.icoop.Extensions;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chest extends AreaEntity implements ICoopInteractionVisitor, Interactable {

    private Sprite chestSprite;
    private ArrayList<AreaEntity> chestContent;

    /**
     * le coffre possède un tableau d'acteurs qui seront libérés lorsqu'il sera ouvert
     */

    public Chest(Area area, Orientation orientation, DiscreteCoordinates position, ArrayList<AreaEntity> content){
        super(area, orientation, position);
        chestContent = content;
        chestSprite = new Sprite("icoop/chestclosed", 1, 1, this);
    }

    /**
     * change le sprite du coffre et libère les objets contenus dedans si un joueur
     * intéragit avec
     */

    public void openChest(){
        chestSprite = new Sprite("icoop/chestopen", 1, 1, this);
        for (AreaEntity entity : chestContent){
            getOwnerArea().registerActor(entity);
        }
    }

    public void update(float deltaTime){
        super.update(deltaTime);
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        chestSprite.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
