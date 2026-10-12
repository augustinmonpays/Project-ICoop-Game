package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.actor.AttackType;
import ch.epfl.cs107.icoop.actor.Foe;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.icoop.actor.ICoopPlayer.MOVE_DURATION;
import static ch.epfl.cs107.play.math.DiscreteCoordinates.distanceBetween;
import static ch.epfl.cs107.play.math.Orientation.*;
import static ch.epfl.cs107.play.math.Orientation.RIGHT;

public class BombFoe extends Foe {
    private BombFoeInteractionHandler handler;
    private OrientedAnimation orientedAnimation;
    public static int MAX_BOMB_FOE_HP = 2;
    private Behavior actualBehavior;
    private final Orientation[] orders = {DOWN, RIGHT, UP, LEFT};
    private static int MAX_INTEGER_CHOISE = 4;
    private double factorOfSpeed;
    private int innactionDuration;
    private ICoopPlayer targetPlayer;
    private int protectTime;


    public BombFoe(Area owner, DiscreteCoordinates coordinates) {
        super(owner, DOWN, coordinates, Arrays.asList(AttackType.PHYSICAL_ATTACK, AttackType.FIRE_ATTACK), MAX_BOMB_FOE_HP);
        this.handler = new BombFoeInteractionHandler();
        this.actualBehavior = Behavior.IDLE;
        this.orientedAnimation = new OrientedAnimation(actualBehavior.getAnimationName(), ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, -0.5f), orders, 3, 2, 2, 32, 32,
                true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (innactionDuration > 0) {
            innactionDuration--;
        }
        if (protectTime > 0) {
            setImmunity(protectTime);
        }

        if (!isDisplacementOccurs()) {
            orientedAnimation.update(dt);
        }

        switch (actualBehavior) {
            case IDLE:
                if (innactionDuration <= 0) {
                    factorOfSpeed = 2;
                    randomMovement();
                    orientedAnimation.update(dt);
                    innactionDuration = RandomGenerator.getInstance().nextInt(1, 26);
                }
                break;

            case ATTACKING:
                if (targetPlayer == null) {
                    actualBehavior = Behavior.IDLE;
                    return;
                }
                targetedMovement();
                orientedAnimation.update(dt);
                /**
                 * si ils sont proches de moins de deux cases le BombFoe pose une bombe
                 */
                if (distanceBetween(this.getCurrentMainCellCoordinates(), targetPlayer.getCurrentMainCellCoordinates()) <= 2) {
                    poseBomb();
                    targetPlayer = null;
                    actualBehavior = Behavior.PROTECTING;
                    this.orientedAnimation = new OrientedAnimation(actualBehavior.getAnimationName(), ANIMATION_DURATION / 3, this,
                            new Vector(-0.5f, -0.5f), orders, 3, 2, 2, 32, 32,
                            false);
                    protectTime = 80;
                }
                break;

            case PROTECTING:
                protectTime--;
                if (protectTime <= 0) {
                    actualBehavior = Behavior.IDLE;
                    this.orientedAnimation = new OrientedAnimation(actualBehavior.getAnimationName(), ANIMATION_DURATION / 3, this,
                            new Vector(-0.5f, -0.5f), orders, 3, 2, 2, 32, 32,
                            true);
                }
                orientedAnimation.update(dt);
                break;
        }
    }


    @Override
    public void draw(Canvas canvas) {
        if (isDead() == Logic.TRUE) {
            super.draw(canvas);
        } else {
            orientedAnimation.draw(canvas);
        }
    }


    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        if (actualBehavior == Behavior.IDLE || actualBehavior == Behavior.ATTACKING) {
            other.acceptInteraction(handler, isCellInteraction);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        switch (actualBehavior) {
            case PROTECTING:
                return List.of();
            case ATTACKING:
                /**
                 * Quand il est en mode attaque on prend juste la case devant lui
                 */
                if (getOrientation().toVector() == null) return List.of(); ;//pour s'assurer qu'il n'y ai pas de NullPointerExeption
                return List.of(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
            case IDLE:
                /**
                 * on construit son champs de vision avec les 8 cellules qui sont directement devant lui
                 */
                List<DiscreteCoordinates> cells = new ArrayList<DiscreteCoordinates>();
                DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
                if (getOrientation().toVector() == null) return List.of();//pour s'assurer qu'il n'y ai pas de NullPointerExeption
                for (int i = 1; i <= 8; i++) {
                    mainCell = mainCell.jump(getOrientation().toVector());
                    cells.add(mainCell);
                }
                return cells;
            default:
                return Collections.emptyList();
        }
    }
    private enum Behavior {
        IDLE ("icoop/bombFoe"),
        ATTACKING("icoop/bombFoe"),
        PROTECTING ("icoop/bombFoe.protecting");


        private String animationName;

        Behavior(String animationName) {
            this.animationName = animationName;
        }

        public String getAnimationName() {
            return animationName;
        }
    }




    @Override
    public boolean wantsCellInteraction() {
        return false;
    }


    @Override
    public boolean wantsViewInteraction() {
        if (actualBehavior == Behavior.PROTECTING) {
            return false;
        }
        return true;
    }

    /**
     * permet de se déplacer aléatoirement en ayant
     * une probabilité 0.4 de changer d'orientation (choisi au hasard) et se déplace toujours de la même distance
     */
    private void randomMovement(){
        if(!isDisplacementOccurs()){
            resetMotion();
            double randomDouble = RandomGenerator.getInstance().nextDouble();
            if (randomDouble < 0.4) {
                Orientation setNewOrientation = Orientation.values()[RandomGenerator.getInstance().nextInt(MAX_INTEGER_CHOISE)];
                orientate(setNewOrientation);
            }
            move((int) (ANIMATION_DURATION/factorOfSpeed));
            innactionDuration++;
        }
    }

    /**
     * Ici lorsqu'on a un joueur en cible on s'approche de lui en utilisant l'explication donnée.
     * Il détermine le vecteur variation entre sa position actuelle et celle de la cible.
     * Ensuite, il choisit la direction horizontale ou verticale en fonction de la plus grande distance.
     * Puis il s'oriente et va vers la cible.
     */
    private void targetedMovement(){
        if (targetPlayer == null) return;
        if (!isDisplacementOccurs()) {
            resetMotion();
            Vector variationVector = targetPlayer.getPosition().sub(getPosition());
            if(Math.abs(variationVector.x) > Math.abs(variationVector.y)){
                orientate(Orientation.fromVector(new Vector(variationVector.x,0)));
            }else{
                orientate(Orientation.fromVector(new Vector(0,variationVector.y)));
            }
            move(MOVE_DURATION/2);
            innactionDuration++;
        }
    }

    /**
     * on vérifie que l'on peut poser une bombe a cette endroit avant de le faire
     * en regardant si elle est pas déja occupée
     */
    private void poseBomb(){
        Explosive spawnBomb = new Explosive(getOwnerArea(), DOWN, getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 60);
        if (getOwnerArea().canEnterAreaCells(spawnBomb, spawnBomb.getCurrentCells())){
            getOwnerArea().registerActor(spawnBomb);
            spawnBomb.activate();
        }
    }

    private class BombFoeInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isPlayerInteraction) {
            actualBehavior = Behavior.ATTACKING;
            targetPlayer = player;
        }
    }
}
