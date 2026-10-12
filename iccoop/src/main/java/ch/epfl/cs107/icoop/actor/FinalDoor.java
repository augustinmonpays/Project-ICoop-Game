package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;

public class FinalDoor extends Door{
    private Logic isOpened;
    private DialogHandler dialogHandler;
    private int timer;

    public FinalDoor(String arrivingArea, Logic area, ArrayList<DiscreteCoordinates> arrivingCoordinates, Area ownerArea, DiscreteCoordinates currentMainCellCoordinates, Orientation orientation, DialogHandler dialogHandler) {
        super(arrivingArea, area, arrivingCoordinates, ownerArea, currentMainCellCoordinates, orientation);
        this.dialogHandler = dialogHandler;
        this.isOpened = area;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * il y a un timer comme ca cela marque une pause entre les moments ou on le dialogue s'active,
     * et il n'y a pas de problème.
     */
    public void publishStayOut(){
        if (timer < 0) {
            dialogHandler.publish(new Dialog("key_required"));
            timer = 50;
        }
    }

    public void publishGoIn(){
        if (timer < 0) {
            dialogHandler.publish(new Dialog("victory"));
            timer = 50;
        }
    }

    public boolean isOpened(){return isOpened.isOn();}

    @Override
    public void update(float dt){
        timer--;
    }
}
