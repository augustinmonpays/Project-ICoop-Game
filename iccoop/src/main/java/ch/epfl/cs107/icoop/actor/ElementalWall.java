package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class ElementalWall extends AreaEntity implements ElementalEntity, Interactable, Interactor {
    private Logic wallIsActive;
    private ElementalWallInteractionHandler handler;
    private Sprite[] wallSprites;
    private String initialElement;
    private String currentElement;
    private PressurePlate pressurePlate;
    private Logic hasAPressurePlate = Logic.FALSE;

    public ElementalWall(Area owner, Orientation orientation, DiscreteCoordinates coordinates, Logic isWallActive, String spriteName, String element) {
        super(owner, orientation, coordinates);
        this.wallIsActive = isWallActive;
        this.wallSprites = RPGSprite.extractSprites(spriteName, 4, 1, 1, this, Vector.ZERO, 256, 256);
        this.initialElement = element;
        handler = new ElementalWallInteractionHandler();
    }


    public ElementalWall(Area owner, Orientation orientation, DiscreteCoordinates coordinates, Logic isWallActive, String spriteName, String element, PressurePlate pressurePlate) {
        this (owner, orientation, coordinates, isWallActive, spriteName, element);
        this.pressurePlate = pressurePlate;
        this.hasAPressurePlate = Logic.TRUE;
    }

    public void update(float deltaTime){
        super.update(deltaTime);

        if (hasAPressurePlate.isOn() && pressurePlate.isOn()) {
            wallIsActive = Logic.FALSE;
            currentElement = "invisible";//lorsque le mur est inactif il prend l'élément invisible qui fait en sorte dans ICoopBehavior, qu'on peut toujours marcher sur un élément de type invisible
        }else{
            wallIsActive = Logic.TRUE;
            currentElement = initialElement;
        }


    }

    @Override
    public String element() {
        return currentElement;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return List.of();
    }

    @Override
    public boolean takeCellSpace() {return false;}

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public boolean isCellInteractable() {
        return wallIsActive.isOn();
    }

    @Override
    public void draw(Canvas canvas) {
        if (wallIsActive.isOn()) {
            wallSprites[getOrientation().ordinal()].draw(canvas);
        }
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public boolean wantsCellInteraction() {
        return wallIsActive.isOn();
    }

    @Override
    public boolean wantsViewInteraction() {
        return wallIsActive.isOn();
    }

    public void destroyWall(){
        getOwnerArea().unregisterActor(this);
    }

    private class ElementalWallInteractionHandler implements ICoopInteractionVisitor{
        @Override
        public void interactWith(ICoopPlayer player, boolean isPlayerInteraction) {
            if (isPlayerInteraction && wallIsActive.isOn()) {
                if (element().equals("water")) {
                       player.attack(1, AttackType.WATER_ATTACK);
                }else
                {
                    player.attack(1, AttackType.FIRE_ATTACK );
                }
            }
        }
    }
}