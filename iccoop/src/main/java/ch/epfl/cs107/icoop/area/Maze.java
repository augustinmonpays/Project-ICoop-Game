package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.Extensions.*;
import ch.epfl.cs107.icoop.actor.*;
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
import java.util.Arrays;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.DOWN;

public class Maze extends ICoopArea {
    private DialogHandler dialogHandler;
    private ChallengeIsCompleted challengeIsCompleted;
    private Logic winCondition = Logic.FALSE;

    public String getTitle(){
        return "Maze";
    }

    public Maze(DialogHandler dialogHandler, ChallengeIsCompleted challengeIsCompleted) {
        this.dialogHandler = dialogHandler;
        this.challengeIsCompleted = challengeIsCompleted;
    }

    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> allPlayerSpawnPositions = new ArrayList<>();
        allPlayerSpawnPositions.add(new DiscreteCoordinates(2, 39));
        allPlayerSpawnPositions.add(new DiscreteCoordinates(3, 39));
        return allPlayerSpawnPositions;
    }

    public void createArea(){
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4,35), Logic.TRUE, "water_wall", "water"));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4,36), Logic.TRUE, "water_wall", "water"));

        PressurePlate pressurePlate4 = new PressurePlate(this, DOWN,new DiscreteCoordinates(6,33));
        registerActor(pressurePlate4);
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(6,35), Logic.TRUE, "fire_wall", "fire", pressurePlate4));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(6,36), Logic.TRUE, "fire_wall", "fire", pressurePlate4));

        registerActor(new StunningPotion(this, Orientation.DOWN, new DiscreteCoordinates(15,35), dialogHandler));
        registerActor(new HealingPotion(this, Orientation.DOWN,new DiscreteCoordinates(15, 19)));


        PressurePlate pressurePlate5 = new PressurePlate(this, DOWN,new DiscreteCoordinates(9,25));//ca peut etre un levier si on fait l'extension
        registerActor(pressurePlate5);

        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(2,34), Logic.TRUE, "fire_wall", "fire"));
        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(3,34), Logic.TRUE, "fire_wall", "fire"));
        registerActor(new Explosive(this, DOWN, new DiscreteCoordinates(6,25),60 ));

        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(5,24), Logic.TRUE, "water_wall", "water"));
        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(6,24), Logic.TRUE, "water_wall", "water"));
        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(8,21), Logic.TRUE, "fire_wall", "fire", pressurePlate5));


        Heart heart1 = new Heart(this, DOWN, new DiscreteCoordinates(15, 18), false);
        Heart heart2 = new Heart(this, DOWN, new DiscreteCoordinates(16, 19), false);
        registerActor(heart1);
        registerActor(heart2);
        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(14, 19), false));
        registerActor(new Heart(this, DOWN, new DiscreteCoordinates(14, 17), false));

        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(8,4), Logic.TRUE, "water_wall", "water"));
        registerActor(new ElementalWall(this, DOWN, new DiscreteCoordinates(13,4), Logic.TRUE, "fire_wall", "fire"));

        registerActor(new BombFoe(this, new DiscreteCoordinates(5,16)));
        registerActor(new BombFoe(this, new DiscreteCoordinates(6,17)));
        registerActor(new BombFoe(this, new DiscreteCoordinates(10,17)));
        registerActor(new BombFoe(this, new DiscreteCoordinates(5,14)));

        List<AttackType> hellSkullVulnerabilities = new ArrayList<>();
        hellSkullVulnerabilities.add(AttackType.PHYSICAL_ATTACK);
        hellSkullVulnerabilities.add(AttackType.WATER_ATTACK);
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12,33), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12,31), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12,29), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12,27), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12,25), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10,33), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10,32), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10,30), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10,28), hellSkullVulnerabilities));
        registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10,26), hellSkullVulnerabilities));


        Staff staff1 = new Staff(this, DOWN, new DiscreteCoordinates(13,2), "fire", "icoop/staff_fire");
        Staff staff2 = new Staff(this, DOWN, new DiscreteCoordinates(8,2), "water", "icoop/staff_water");
        registerActor(staff1);
        registerActor(staff2);


        ArrayList<DiscreteCoordinates> startingCoordinates2 = new ArrayList<>();
        startingCoordinates2.add (new DiscreteCoordinates(4,5));
        startingCoordinates2.add(new DiscreteCoordinates(14,15));
        registerActor(new Door("Arena", Logic.TRUE, startingCoordinates2, this, new DiscreteCoordinates (19, 6), DOWN, new DiscreteCoordinates(19, 7)));

        winCondition = new And(staff1, staff2);

        Lever lever1 = new Lever(this, DOWN, new DiscreteCoordinates(10,12));
        Lever lever2 = new Lever(this, DOWN, new DiscreteCoordinates(10,31));
        Lever lever3 = new Lever(this, DOWN, new DiscreteCoordinates(15,21));
        Lever lever4 = new Lever(this, DOWN, new DiscreteCoordinates(4,6));
        registerActor(lever1);
        registerActor(lever2);
        registerActor(lever3);
        registerActor(lever4);
        registerActor(new LittleBoy(this, DOWN, new DiscreteCoordinates(6,6),lever1, lever2, lever3, lever4 ));

        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(8, 5)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(8, 6)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(8, 7)));

        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(9, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(9, 6)));

        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(10, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(10, 6)));

        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(11, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(11, 6)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(12, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(12, 6)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(13, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(13, 6)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(14, 7)));
        registerActor(new Stone(this, DOWN, new DiscreteCoordinates(14, 6)));
    }
    @Override
    public boolean isOn() {
        return winCondition.isOn();
    }

    @Override
    public boolean isOff() {
        return false;
    }
}



