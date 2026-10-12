package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public class Door extends AreaEntity implements ICoopInteractionVisitor {
    private String arrivingArea;
    private ArrayList<DiscreteCoordinates> arrivingCoordinates = new ArrayList<>();
    private DiscreteCoordinates[] otherCoordinates;
    private Logic open;
    private DiscreteCoordinates currentMainCellCoordinates;

    public Door(String arrivingArea, Logic signal, ArrayList<DiscreteCoordinates> arrivingCoordinates, Area ownerArea, DiscreteCoordinates currentMainCellCoordinates, Orientation orientation) {
        super(ownerArea, orientation, currentMainCellCoordinates);
        this.currentMainCellCoordinates = currentMainCellCoordinates;
        this.arrivingArea = arrivingArea;
        open = signal;
        this.arrivingCoordinates = arrivingCoordinates;
    }

    public Door(String arrivingArea, Logic signal, ArrayList<DiscreteCoordinates> arrivingCoordinates, Area ownerArea,
                DiscreteCoordinates currentMainCellCoordinates, Orientation orientation, DiscreteCoordinates...otherCoordinates) {
        this(arrivingArea, signal, arrivingCoordinates, ownerArea, currentMainCellCoordinates, orientation);
        this.otherCoordinates = otherCoordinates;

    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * ses coordonnées sont celles de sa cellule principale ainsi que les autres coordonnées qu'on
     * a l'option de mettre en plus
     * @return
     */
    @Override
    public ArrayList<DiscreteCoordinates> getCurrentCells() {
        ArrayList<DiscreteCoordinates> allCoordinates = new ArrayList<>();
        allCoordinates.add(currentMainCellCoordinates);
        if (otherCoordinates != null){
        for (DiscreteCoordinates coordinates : otherCoordinates){
            allCoordinates.add(coordinates);
        }
        }
        return allCoordinates;
    }

    public ArrayList<DiscreteCoordinates> getSpawnCoordinates(){
        return arrivingCoordinates;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    public String getArea(){
        return arrivingArea;
    }

    public boolean isOn(){
        if (open == Logic.TRUE){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isOff(){
        if (open == Logic.TRUE){
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}

