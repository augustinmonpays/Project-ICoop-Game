package ch.epfl.cs107.icoop.actor;

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

public class PressurePlate extends AreaEntity implements Logic {

    private Sprite plateSprite;
    private boolean isPressed;


    public PressurePlate(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position);
        plateSprite = new Sprite("GroundPlateOff", 1, 1, this);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas){
        plateSprite.draw(canvas);
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
        return isPressed;
    }

    @Override
    public boolean isOff() {
        return !isPressed;
    }

    public void activatePressurePlate(){
        this.isPressed = true;
    }

    public void deactivatePressurePlate(){
        this.isPressed = false;
    }

    }