package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;

/**
 * Nous avons créée cet Acteur car c'est un outil pratique pour :
 * - plus facile de faire l'effet de tremblement
 * en creeant des acteurs qui ont seulement des coordonnées (que nous pouvons choisir au hasard). Donc
 * que nous pouvons utiliser pour créer un nouveau centre de masse temporaire qui changera la vue
 * - plus facile de mettre les animations exactement ou on veut (en mettant que la parent de l'animation
 * est un Invisible Actor exactement ou on veut)
 */
public class InvisibleActor extends Entity implements Actor {

    public InvisibleActor(Vector position) {
        super(position);
    }

}
