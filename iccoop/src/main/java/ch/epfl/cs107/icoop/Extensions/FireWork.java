package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;


import static ch.epfl.cs107.icoop.actor.Foe.ANIMATION_DURATION;

public class FireWork extends InvisibleActor {
    Animation fireWorkAnimation;
    private float delay; // Temps avant le début de l'animation
    private float timeElapsed; // Temps écoulé depuis le début

    public FireWork(Vector position, int width, int height, String spriteName) {
        super(position);
        this.fireWorkAnimation = new Animation(spriteName, 7, width, height, this, 128, 128, ANIMATION_DURATION / 7, true);

        // Ajout d'un délai aléatoire entre 0 et 3 secondes
        this.delay = RandomGenerator.getInstance().nextFloat(3);
        this.timeElapsed = 0.0f;
    }

    @Override
    public void draw(Canvas canvas) {
        if (timeElapsed >= delay) {
            fireWorkAnimation.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Mettre à jour le temps écoulé
        timeElapsed += deltaTime;


        // Si le délai est écoulé, mettre à jour l'animation
        if (timeElapsed >= delay) {
            fireWorkAnimation.update(deltaTime);
        }
    }
}
