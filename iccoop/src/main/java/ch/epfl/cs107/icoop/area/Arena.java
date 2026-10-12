package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.Extensions.HealingPotion;
import ch.epfl.cs107.icoop.Extensions.QuakePotion;
import ch.epfl.cs107.icoop.actor.Key;
import ch.epfl.cs107.icoop.actor.Teleporter;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.DOWN;

public class Arena extends ICoopArea{
    private ChallengeIsCompleted challengeIsCompleted;
    private DialogHandler dialogHandler;
    private Logic alreadyPublished = Logic.FALSE;
    private Logic winCondition = Logic.FALSE;

    public Arena(DialogHandler dialogHandler, ChallengeIsCompleted challengeIsCompleted) {
        this.dialogHandler = dialogHandler;
        this.challengeIsCompleted = challengeIsCompleted;
    }


    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));


        Key key1 = new Key(this, DOWN, new DiscreteCoordinates(9,16), "fire");
        Key key2 = new Key(this, DOWN, new DiscreteCoordinates(9,4), "water");
        registerActor(key1);
        registerActor(key2);
        ArrayList<DiscreteCoordinates> arriveCoordinates = new ArrayList<>();
        arriveCoordinates.add(new DiscreteCoordinates(13, 6));
        arriveCoordinates.add(new DiscreteCoordinates(14, 6));
        registerActor(new Teleporter("Spawn", Logic.FALSE, arriveCoordinates, this, new DiscreteCoordinates(10,11), DOWN, key1, key2));
        registerActor(new QuakePotion(this, Orientation.DOWN, new DiscreteCoordinates(14,14), dialogHandler));
        registerActor(new HealingPotion(this, Orientation.DOWN, new DiscreteCoordinates(9, 10)));


        winCondition = new And(key1, key2);

    }

    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> allPlayerSpawnPositions = new ArrayList<>();
        allPlayerSpawnPositions.add(new DiscreteCoordinates(4, 5));
        allPlayerSpawnPositions.add(new DiscreteCoordinates(14, 15));
        return allPlayerSpawnPositions;
    }

    @Override
    public String getTitle() {
        return "Arena";
    }

    @Override
    public boolean isOn() {
        return winCondition.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}
