package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;
import static ch.epfl.cs107.play.math.Orientation.LEFT;

public abstract class Foe extends MovableAreaEntity implements Interactor, ICoopInteractionVisitor {

    private Animation deathAnimation;
    private Health health;
    private KeyBindings.PlayerKeyBindings key;
    public final static int ANIMATION_DURATION = 24;
    private Logic isDead = Logic.FALSE;
    private List<AttackType> vulnerableAttacks = new ArrayList<>();
    private int immunity;
    public static int DEFAULT_MAX_LIFE = 2;


    /**
     * Il y a deux constructeurs au cas ou le niveau de vie maximal n'est pas spécifié
     * il y en a un par défaut
     * @param owner
     * @param orientation
     * @param coordinates
     * @param vulnerableAttacks
     * @param maxLife
     */
    public Foe(Area owner, Orientation orientation, DiscreteCoordinates coordinates, List<AttackType> vulnerableAttacks, int maxLife) {
        super(owner, orientation, coordinates);
        this.health = new Health(this, Transform.I.translated(0, 1.75f), maxLife, false);
        this.vulnerableAttacks = vulnerableAttacks;
        deathAnimation = new Animation("icoop/vanish", 7, 1, 1, this, 32, 32, ANIMATION_DURATION / 7, false);
    }

    public Foe(Area owner, Orientation orientation, DiscreteCoordinates coordinates, List<AttackType> vulnerableAttacks) {
        this(owner, orientation,coordinates, vulnerableAttacks, DEFAULT_MAX_LIFE);
    }

    private void die(){
        isDead = Logic.TRUE;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        immunity--;
        if (health.getCurrentHealth() == 0){
            die();
            deathAnimation.update(deltaTime); //on attend la fin de l'animation pour unregister l'actor
            if (deathAnimation.isCompleted()){
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public void draw (Canvas canvas) {
        if(isDead.isOn()){
            deathAnimation.draw(canvas);
        }
        else {
            super.draw(canvas);
        }
    }

    public void setImmunity(int timer){
        immunity = timer;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public abstract void interactWith(Interactable other, boolean isCellInteraction);

    @Override
    public boolean takeCellSpace() {return isDead.isOff();}

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public void attack(int damage, AttackType attackType) {
        if(immunity <=0) {
            if ((attackType == AttackType.WATER_ATTACK) && isVulnerableTo(AttackType.WATER_ATTACK)) {
                health.decrease(damage);
                setImmunity(24);
            }
            if ((attackType == AttackType.FIRE_ATTACK) && isVulnerableTo(AttackType.FIRE_ATTACK)) {
                health.decrease(damage);
                setImmunity(24);
            }
            if ((attackType == AttackType.PHYSICAL_ATTACK)&& isVulnerableTo(AttackType.PHYSICAL_ATTACK)){
                health.decrease(damage);
                setImmunity(24);
            }
        }
    }

    private boolean isVulnerableTo(AttackType attackType){
        for (AttackType attack : vulnerableAttacks){
            if (attack.equals(attackType)){return true;}
        }
        return false;
    }

    public Logic isDead(){
        return isDead;
    }


}


