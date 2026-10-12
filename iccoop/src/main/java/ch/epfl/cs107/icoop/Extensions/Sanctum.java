package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;

import static ch.epfl.cs107.play.math.Orientation.DOWN;

public class Sanctum extends ICoopArea {
    private DialogHandler dialogHandler;


    public Sanctum(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));

        ArrayList<AreaEntity> chest1Content = new ArrayList<>();
        Bier bierInChest1 = new Bier(this, DOWN, new DiscreteCoordinates(9, 10), dialogHandler);
        chest1Content.add(bierInChest1);
        registerActor(new Chest(this, Orientation.DOWN, new DiscreteCoordinates(8, 10), chest1Content ));

        ArrayList<AreaEntity> chest2Content = new ArrayList<>();
        Bier bierInChest2 = new Bier(this, DOWN, new DiscreteCoordinates(6, 10), dialogHandler);
        chest2Content.add(bierInChest2);
        registerActor(new Chest(this, Orientation.DOWN, new DiscreteCoordinates(7, 10), chest2Content ));

        registerActor(new HealingPotion(this, DOWN, new DiscreteCoordinates(3, 12)));
        registerActor(new HealingPotion(this, DOWN, new DiscreteCoordinates(12, 12)));

        ArrayList<DiscreteCoordinates> startingCoordinates = new ArrayList<>();
        startingCoordinates.add(new DiscreteCoordinates(9,13));
        startingCoordinates.add(new DiscreteCoordinates(10,13));
        registerActor(new Door("SanctumEntrance", Logic.TRUE, startingCoordinates, this, new DiscreteCoordinates (7, 0), DOWN, new DiscreteCoordinates(8,0)));

        registerActor(new FireWork(new Vector(11,8), 1,1, "icoop/fireworkone"));
        registerActor(new FireWork(new Vector(13,3), 1,1, "icoop/fireworktwo"));

        registerActor(new FireWork(new Vector(5,4), 2,2, "icoop/fireworkthree"));
        registerActor(new FireWork(new Vector(1,11), 2,2, "icoop/fireworkone"));

        registerActor(new FireWork(new Vector(12,4), 3,3, "icoop/fireworkfour"));
        registerActor(new FireWork(new Vector(13,9), 3,3, "icoop/fireworkone"));
        registerActor(new FireWork(new Vector(14,12), 3,3, "icoop/fireworktwo"));

        registerActor(new FireWork(new Vector(1,3), 3,3, "icoop/fireworktwo"));
        registerActor(new FireWork(new Vector(2,8), 3,3, "icoop/fireworkthree"));
        registerActor(new FireWork(new Vector(4,13), 3,3, "icoop/fireworkfour"));

        registerActor(new FireWork(new Vector(4, 16), 2, 2, "icoop/fireworkfour"));
        registerActor(new FireWork(new Vector(15, 17), 2, 2, "icoop/fireworkthree"));
    }

    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> respawnPosition = new ArrayList<>();
        respawnPosition.add(new DiscreteCoordinates(7,1));
        respawnPosition.add(new DiscreteCoordinates(8,1));
        return respawnPosition;
    }

    @Override
    public String getTitle() {
        return "Sanctum";
    }

    @Override
    public boolean isOn() {
        return false;
    }

    @Override
    public boolean isOff() {
        return false;
    }
}