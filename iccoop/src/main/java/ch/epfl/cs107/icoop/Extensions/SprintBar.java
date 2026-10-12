package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.math.Positionable;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.shape.Polygon;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.List;

/**
 * une sprint bar c'est presque exactement comme une health bar, mais elle est déssinée un peu plus haut que la health bar
 */
public class SprintBar implements Graphics, Logic {

    private final Positionable parent;
    private final Transform transform;
    private final int maxHealth;
    private int healthPoints;


    public SprintBar(Positionable parent, Transform transform, int healthPoints) {
        this.parent = parent;
        this.transform = transform;
        this.maxHealth = healthPoints;
        this.healthPoints = healthPoints;
    }


    @Override
    public boolean isOn() {
        return healthPoints != 0;
    }


    @Override
    public boolean isOff() {
        return !isOn();
    }


    @Override
    public float getIntensity() {
        return isOn() ? 1 : 0;
    }


    /**
     * change le visuel de la bar de sprint en la remplissant ou la vidant
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        // draws the grey outline
        canvas.drawShape(new Polygon(List.of(
                new Vector(0.1f, 0),
                new Vector(0.9f, 0),
                new Vector(0.9f, 0.15f),
                new Vector(0.1f, 0.15f)
        )), parent.getTransform().transformed(transform), Color.GRAY, Color.BLACK, 0.1f, 1, 0);
        // draws the green / red bar
        canvas.drawShape(new Polygon(List.of(
                new Vector(0.1f, 0),
                new Vector(0.8f * healthPoints / maxHealth + 0.1f, 0),
                new Vector(0.8f*  healthPoints / maxHealth + 0.1f, 0.15f),
                new Vector(0.1f, 0.15f)
        )), parent.getTransform().transformed(transform), Color.ORANGE , null, 0, 0.5f, 0);
    }

    /**
     * remplit la barre d'endurance
     */
    public void increase(int amount) {
        healthPoints = Math.min(maxHealth, healthPoints + amount);
    }

    /**
     * vide la barre d'endurance
     * @param amount
     */
    public void decrease(int amount) {
        healthPoints = Math.max(0, healthPoints - amount);
    }

    public int CurrentSprintRemaining(){
        return healthPoints;
    }

}

