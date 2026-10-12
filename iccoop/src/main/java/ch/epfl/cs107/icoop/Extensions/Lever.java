package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Lever extends AreaEntity implements Logic {

    private Sprite leverSprite;
    private Logic isActivated;


    public Lever(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position);
        leverSprite = new Sprite("LeverDown", 1, 1, this);
        this.isActivated = Logic.FALSE;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
    }

    /**
     * En fonction de son état on le dessine dans différentes positions
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas){
        if(isActivated.isOn()){
            this.leverSprite = new Sprite("LeverUp", 1, 1, this);
        }else{
            this.leverSprite = new Sprite("LeverDown", 1, 1, this);
        }
        leverSprite.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return false;
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
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public boolean isOn() {
        return isActivated.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    /**
     * on fait toggle car comme il a deux position, quand on l'active
     * on veut forcément qu'il aille dans l'autre position
     */
    public void toggleLever(){
        if(isActivated.isOn()){
            isActivated=Logic.FALSE;
        }else{
            isActivated=Logic.TRUE;
        }
    }
}
