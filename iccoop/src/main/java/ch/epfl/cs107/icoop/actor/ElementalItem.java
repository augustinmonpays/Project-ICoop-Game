package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

abstract public class ElementalItem extends ICoopCollectable implements ElementalEntity, Logic {
    private String element;


    public ElementalItem(Area area, Orientation orientation, DiscreteCoordinates position, String element) {
        super(area, orientation, position);
        this.element = element;
    }

    @Override
    public String element() {
        return element;
    }

    @Override
    public boolean isOn(){return isCollected();}

    @Override
    public boolean isOff(){return !isCollected();}

    /**
     * on vérifie que l'élément concondre pour le ramasser
     * @param element
     */
    public void collect(String element) {
        if (element.equals(this.element)) {super.collect();}
    }

}
