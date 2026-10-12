package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

abstract public class ICoopCollectable extends CollectableAreaEntity {

    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }

    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected) {
        super(area, orientation, position, isCollected);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public boolean isOn(){return isCollected();}
    public boolean isOff(){return !isCollected();}

}


