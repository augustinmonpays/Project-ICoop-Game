package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;

public class CenterOfMass implements Actor {

    private final Actor[] actors;

    /**
     * on met l'acteur et les autres acteurs dans un même tableau
     * @param actor
     * @param restOfActors
     */
    public CenterOfMass(Actor actor, Actor... restOfActors) {
        this.actors = new Actor[restOfActors.length + 1];
        actors[0] = actor;
        System.arraycopy(restOfActors, 0, this.actors, 1, restOfActors.length);
    }

    /**
     * calcule la position moyenne de tous les acteurs dans le tableau
     * @return
     */
    @Override
    public Vector getPosition() {
        Vector position = Vector.ZERO;
        for (Actor actor : actors) {
            position = position.add(actor.getPosition());
        }
        return position.mul(1f / actors.length);
    }

    @Override
    public Transform getTransform() {
        return Transform.I.translated(getPosition());
    }

    /**
     * vitesse moyenne de tous les acteurs
     * @return
     */
    @Override
    public Vector getVelocity() {
        Vector velocity = Vector.ZERO;
        for (Actor actor : actors) {
            velocity = velocity.add(actor.getVelocity());
        }
        return velocity.mul(1f / actors.length);
    }
}
