package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;
import static ch.epfl.cs107.play.math.Orientation.LEFT;

public class HellSkull extends Foe {
    public static final int ANIMATION_DURATION = 12;
    private OrientedAnimation orientedAnimation;
    private float randomFloat;
    public static final float MIN = 0.5f, MAX = 2.0f;
    private HellSkullInteractionHandler handler;

    public HellSkull(Area owner, Orientation orientation, DiscreteCoordinates coordinates, List<AttackType> vulnerableAttacks) {
        super (owner, orientation, coordinates, vulnerableAttacks);
        this.handler = new HellSkullInteractionHandler();

        final Orientation[] orders = {UP, LEFT, DOWN, RIGHT};
        orientedAnimation = new OrientedAnimation("icoop/flameskull", ANIMATION_DURATION/3, this,
                new Vector(-0.5f,-0.5f), orders, 3, 2, 2, 32, 32,
                true);

    }

    /**
     * on génère de manière périodique des Fire que l'on place sur la position
     * actuelle du HellSkull
     * @param dt elapsed time since last update, in seconds, non-negative
     */

    @Override
    public void update(float dt){
        super.update(dt);
        randomFloat -= dt;
        if (randomFloat < 0){
            getOwnerArea().registerActor(new Fire (getOwnerArea(), getOrientation(),getCurrentMainCellCoordinates(),1,45));
            randomFloat =  RandomGenerator.getInstance().nextFloat(MIN, MAX);
        }
    }

    @Override
    public void draw (Canvas canvas){
        if (isDead() == Logic.TRUE){
            super.draw(canvas);
        }
        else {
            orientedAnimation.draw(canvas);
        }
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates position = new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y);
        return position.getNeighbours();
    }

    /**
     * il n'intéragit avec Personne
     */
    private class HellSkullInteractionHandler implements ICoopInteractionVisitor {
    }

}