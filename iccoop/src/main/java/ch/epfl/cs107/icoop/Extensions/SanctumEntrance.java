package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.AttackType;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Heart;
import ch.epfl.cs107.icoop.actor.Key;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.DOWN;

public class SanctumEntrance extends ICoopArea {
private DialogHandler dialogHandler;
private Logic areaIsActive;


    public SanctumEntrance(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    @Override
    protected void createArea() { //10,15 ou 16
        registerActor(new Background(this));
        registerActor(new WaterFall(this, DOWN, new DiscreteCoordinates(13,3)));
        registerActor(new WaterFall(this, DOWN, new DiscreteCoordinates(16,3)));
        registerActor(new WaterFall(this, DOWN, new DiscreteCoordinates(1,5)));


        ArrayList<DiscreteCoordinates> startingCoordinates = new ArrayList<>();
        startingCoordinates.add(new DiscreteCoordinates(7,1));
        startingCoordinates.add(new DiscreteCoordinates(8,1));
        Lever lever1 = new Lever(this, DOWN, new DiscreteCoordinates(5,12));
        Lever lever2 = new Lever(this, DOWN, new DiscreteCoordinates(14,12));
        registerActor(lever1);
        registerActor(lever2);

        registerActor(new SanctumDoor("Sanctum", this, startingCoordinates,this, new DiscreteCoordinates(10, 14 ), DOWN, dialogHandler));
        this.areaIsActive = new And(lever1, lever2);
        ArrayList<DiscreteCoordinates> startingCoordinates2 = new ArrayList<>();
        startingCoordinates2.add(new DiscreteCoordinates(6,9));
        startingCoordinates2.add(new DiscreteCoordinates(7,9));
        registerActor(new Door("Spawn", Logic.TRUE, startingCoordinates2, this, new DiscreteCoordinates (9, 1), DOWN, new DiscreteCoordinates(10,1)));

        ArrayList<AttackType> logVulnerabilities = new ArrayList<>();
        logVulnerabilities.add(AttackType.PHYSICAL_ATTACK);
        logVulnerabilities.add(AttackType.FIRE_ATTACK);
        registerActor(new logMonster(this, Orientation.RIGHT, new DiscreteCoordinates(7, 6), logVulnerabilities, 5));
        registerActor(new logMonster(this, Orientation.DOWN, new DiscreteCoordinates(11, 8), logVulnerabilities, 5));
        registerActor(new logMonster(this, Orientation.LEFT, new DiscreteCoordinates(13, 12), logVulnerabilities, 5));
        registerActor(new logMonster(this, Orientation.UP, new DiscreteCoordinates(5, 9), logVulnerabilities, 5));
        registerActor(new logMonster(this, Orientation.DOWN, new DiscreteCoordinates(9, 14), logVulnerabilities, 5));

        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(10, 3), false));
        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(7, 4), false));
        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(11,9 ), false));
        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(9, 13), false));

    }

    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> respawnPosition = new ArrayList<>();
        respawnPosition.add(new DiscreteCoordinates(10,3));
        respawnPosition.add(new DiscreteCoordinates(9,3));
        return respawnPosition;
    }

    @Override
    public String getTitle() {
        return "SanctumEntrance";
    }

    @Override
    public boolean isOn() {
        return areaIsActive.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}
