package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.Extensions.*;
import ch.epfl.cs107.icoop.ICoop;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.DOWN;

public class Spawn extends ICoopArea {
    private ChallengeIsCompleted challengeIsCompleted;
    private DialogHandler dialogHandler;
    private Logic alreadyPublished = Logic.FALSE;

    public Spawn(DialogHandler dialogHandler, ChallengeIsCompleted challengeIsCompleted) {
        this.dialogHandler = dialogHandler;
        this.challengeIsCompleted = challengeIsCompleted;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(alreadyPublished.isOff()) {
            dialogHandler.publish(new Dialog("welcome"));
            alreadyPublished = Logic.TRUE;
        }
    }


    public String getTitle(){
        return "Spawn";
    }

    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {//La méthode renvoi une ArrayList de coordonées des positions de spawn des joueurs. C'est une ArrayList car ce nombre pourrait augmenter.
        ArrayList<DiscreteCoordinates> allPlayerSpawnPositions = new ArrayList<>();
        allPlayerSpawnPositions.add(new DiscreteCoordinates(13, 6));
        allPlayerSpawnPositions.add(new DiscreteCoordinates(14, 6));
        return allPlayerSpawnPositions;
    }

    public void createArea(){
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ArrayList<DiscreteCoordinates> startingCoordinates = new ArrayList<>(); //création tableau contenant coordonnées principales
        startingCoordinates.add(new DiscreteCoordinates(1,12)); //remplissage du tableau
        startingCoordinates.add(new DiscreteCoordinates(1,5));

        registerActor(new Door("OrbWay", Logic.TRUE, startingCoordinates, this, new DiscreteCoordinates (19, 15), DOWN, new DiscreteCoordinates(19, 16)));

        ArrayList<DiscreteCoordinates> startingCoordinates2 = new ArrayList<>();
        startingCoordinates2.add(new DiscreteCoordinates(2,39));
        startingCoordinates2.add(new DiscreteCoordinates(3,39));
        registerActor(new Door("Maze", Logic.TRUE, startingCoordinates2, this, new DiscreteCoordinates (4, 0), DOWN, new DiscreteCoordinates(5, 0)));

        ArrayList<DiscreteCoordinates> startingCoordinates3 = new ArrayList<>();
        startingCoordinates3.add(new DiscreteCoordinates(9,3));
        startingCoordinates3.add(new DiscreteCoordinates(10,3 ));

        /**
         * L'état de la FinalDoor (On ou Off) dépend de l'état de Spawn.
         * Ainsi pour que la FinalDoor soit ouverte il faut que Spawn soit On,
         * Soit que "challengeIsCompleted.isCompleted()" renvoi true
         */
        registerActor(new FinalDoor("SanctumEntrance", this, startingCoordinates3, this, new DiscreteCoordinates(6,11), DOWN, dialogHandler));
    }

    @Override
    public boolean isOn() {
        return challengeIsCompleted.isCompleted();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}