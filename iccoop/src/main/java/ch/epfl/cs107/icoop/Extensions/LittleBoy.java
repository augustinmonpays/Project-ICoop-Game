package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LittleBoy extends ICoopCollectable implements Interactor {
    private static final int ANIMATION_DURATION = 24;
    private int timer;
    private Logic timerActivated = Logic.FALSE;
    private Logic exploding = Logic.FALSE;
    private Logic exploded = Logic.FALSE;
    private Animation animatedExplosion;
    private LittleBoyInteractionHandler handler;
    private Logic canExplode;
    private Logic canExplode2; //J'ai fais un deuxieme Logic car je ne savais pas comment faire pour inclure quatre leviers dans un seul "And"


    public LittleBoy(Area owner, Orientation orientation, DiscreteCoordinates coordinates, Lever lever1, Lever lever2, Lever lever3, Lever lever4) {
        super(owner, orientation, coordinates);
        this.timer = 40;
        /**
         * ici les regions width/height sont a 128 car nous avons créée des Sprite sur les cases de 128 par 128 pixel sur gimp.
         * et souvent les animations/sprites que nous avons fait nous même auront ces dimensions car nous avons gardés le
         * même fond pour chacun
         */
        animatedExplosion = new Animation("icoop/LittleBoy", 7, 2, 2, this, 128, 128,
                ANIMATION_DURATION /4, false);
        handler = new LittleBoyInteractionHandler();
        /**
         * son état (On/Off) dépend des quatres leviers qu'il prend en constructeur
         */
        this.canExplode = new And(lever1, lever2);
        this.canExplode2 = new And(lever3, lever4);
    }


    /**
     * ce fonctionnement est identique a Explosive a part le fait
     * que l'explosion est une animation différente
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(canExplode.isOn() && canExplode2.isOn()) {
            activate();
        }
        if (timerActivated.isOn() && exploding.isOff()) {
            timer--;
            animatedExplosion.update(deltaTime);
            if (timer == 0) {
                animatedExplosion = new Animation("icoop/megaexplosion", 7, 5, 5, new InvisibleActor(new Vector(4,6)), 128, 128,
                        ANIMATION_DURATION /7, false);
                exploded = Logic.TRUE;
                exploding = Logic.TRUE;
            }
        }

        if (exploding.isOn()) {
            animatedExplosion.update(deltaTime);
            if (animatedExplosion.isCompleted()) {
                getOwnerArea().unregisterActor(this);
                exploding = Logic.FALSE;
                timerActivated = Logic.FALSE;
            }
        }

        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Il a un très grand champs de vision car on imite une très grosse bombe
     * @return
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();

        // On fait comme pour les BombeFoe qui ont une vision de 8 devant eux, mais ici ca sera vingt et un dans toutes les directions
        for (int dx = -21; dx <= 21; dx++) {
            for (int dy = -21; dy <= 21; dy++) {
                if (dx != 0 || dy != 0) {
                    DiscreteCoordinates cell = mainCell.jump(dx, dy);
                    cells.add(cell);
                }
            }
        }
        return cells;
    }

    @Override
    public boolean takeCellSpace() {
        return (!exploding.isOn());
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public boolean wantsCellInteraction() {
        return exploded.isOn();
    }

    @Override
    public boolean wantsViewInteraction() {
        return exploding.isOn();
    }


    @Override
    public void draw(Canvas canvas) {
        animatedExplosion.draw(canvas);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    public void activate() {
        this.timerActivated = Logic.TRUE;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    private class LittleBoyInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(Stone stone, boolean b) {
            stone.destroy();
        }

        @Override
        public void interactWith(ICoopPlayer player, boolean b){
            player.attack(1, AttackType.PHYSICAL_ATTACK);
            player.longQuake();
        }

        @Override
        public void interactWith(Explosive explosif, boolean b){
            explosif.chainExplosion();
        }

        @Override
        public void interactWith(Foe foe, boolean b){
            foe.attack(2, AttackType.PHYSICAL_ATTACK);}

    }
}



