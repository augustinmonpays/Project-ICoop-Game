package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.Extensions.StunningPotion;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;

public class OrbWay extends ICoopArea {
    private DialogHandler dialogHandler;
    private Logic winCondition = Logic.FALSE;
    private ChallengeIsCompleted challengeIsCompleted;

    @Override
    public String getTitle(){
        return "OrbWay";
    }

    public OrbWay(DialogHandler dialogHandler, ChallengeIsCompleted challengeIsCompleted){
        this.dialogHandler = dialogHandler;
        this.challengeIsCompleted = challengeIsCompleted;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
    }

    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> allPlayerSpawnPositions = new ArrayList<>();
        allPlayerSpawnPositions.add(new DiscreteCoordinates(1, 12));
        allPlayerSpawnPositions.add(new DiscreteCoordinates(1, 5));
        return allPlayerSpawnPositions;
    }
    @Override
    public void createArea(){
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ArrayList<DiscreteCoordinates> startingCoordinatesSpawn1 = new ArrayList<>(); //création tablwater coordonnées principales
        startingCoordinatesSpawn1.add(new DiscreteCoordinates(18,16)); //ajout des coordonnées
        startingCoordinatesSpawn1.add(new DiscreteCoordinates(18,15));

        registerActor(new Door("Spawn", Logic.TRUE, startingCoordinatesSpawn1, this, new DiscreteCoordinates (0, 14), Orientation.UP, new DiscreteCoordinates(0, 13), new DiscreteCoordinates(0, 12),new DiscreteCoordinates(0, 11),new DiscreteCoordinates(0, 10)));

        ArrayList<DiscreteCoordinates> startingCoordinatesSpawn2 = new ArrayList<>(); //création tablwater coordonnées principales
        startingCoordinatesSpawn2.add(new DiscreteCoordinates(18,16));//ajout des coordonnées
        startingCoordinatesSpawn2.add(new DiscreteCoordinates(18,15));

        Orb orb1 = new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17,12), "fire", dialogHandler);
        Orb orb2 = new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17,6), "water",dialogHandler);
        registerActor(new Door ("Spawn", Logic.TRUE, startingCoordinatesSpawn2, this, new DiscreteCoordinates (0, 8), Orientation.UP, new DiscreteCoordinates(0, 7),new DiscreteCoordinates(0, 6),new DiscreteCoordinates(0, 5),new DiscreteCoordinates(0, 4)));
        registerActor(orb1);
        registerActor(orb2);

        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(8, 4), false));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 6), false));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(5, 13), false));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 11), false));


        PressurePlate pressurePlate1 = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5,7));
        registerActor(pressurePlate1);
        PressurePlate pressurePlate2 = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 10));
        registerActor(pressurePlate2);

        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,10), Logic.TRUE, "fire_wall", "fire", pressurePlate1));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,11), Logic.TRUE, "fire_wall", "fire", pressurePlate1));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,12), Logic.TRUE, "fire_wall", "fire",pressurePlate1));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,13), Logic.TRUE, "fire_wall", "fire", pressurePlate1));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,14), Logic.TRUE, "fire_wall", "fire",pressurePlate1 ));

        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,4), Logic.TRUE, "water_wall", "water", pressurePlate2));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,5), Logic.TRUE, "water_wall", "water", pressurePlate2));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,6), Logic.TRUE, "water_wall", "water", pressurePlate2));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,7), Logic.TRUE, "water_wall", "water", pressurePlate2));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12,8), Logic.TRUE, "water_wall", "water", pressurePlate2));

        winCondition = new And(orb1, orb2);
    }

    @Override
    public boolean isOn() {
        return winCondition.isOn();
    }

    @Override
    public boolean isOff() {
        return winCondition.isOff();
    }
}


