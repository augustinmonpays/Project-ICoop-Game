package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Unstoppable;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public class SanctumDoor extends Door {
    private Logic isOpened;
    private DialogHandler dialogHandler;
    private int timer; //pour mettre du temps entre les fois ou on peut actionner le dialogue
    private Sprite openedDoorSprite;
    private Logic dialogIsAlreadyPublished = Logic.FALSE;

    public SanctumDoor(String aireDarrivee, Logic area, ArrayList<DiscreteCoordinates> coordoneeDarrivee, Area ownerArea, DiscreteCoordinates currentMainCellCoordinates, Orientation orientation, DialogHandler dialogHandler) {
        super(aireDarrivee, area, coordoneeDarrivee, ownerArea, currentMainCellCoordinates, orientation);
        this.dialogHandler = dialogHandler;
        this.isOpened = area;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * lance un dialogue quand on intéragit avec la porte
     * le timer permet de faire en sorte que les dialogues ne se lancent pas en boucle
     */

    public void publishGoIn(){
        if (timer < 0) {
            if(dialogIsAlreadyPublished.isOff()) {
                dialogHandler.publish(new Dialog("finalVictory"));
            }
            timer = 50;
        }
    }

    /**
     * choisit le sprite de la porte a dessiné selon si elle est ouverte ou non
     */
    @Override
    public void update(float deltaTime) {
        timer--;
        if(isOpened.isOn()){
            this.openedDoorSprite = new Sprite ("icoop/opendoor", 3, 3, new InvisibleActor(new Vector(9,16)));// on creer un Invisible Actor pour correctement positionner le sprite de la Door
        }
        else {
            this.openedDoorSprite = new Sprite ("icoop/closeddoor", 3, 3, new InvisibleActor(new Vector(9,16))) ;
        }
    }

    public void draw(Canvas canvas){
        if(openedDoorSprite != null){
            openedDoorSprite.draw(canvas);
        }
    }

    public boolean isOn(){
        return isOpened.isOn();
    }

}

