package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Arrow extends Projectiles {
    private ArrowInteractionHandler handler;
    private String spriteName;
    private Sprite arrowSprite;

    public Arrow(Area area, Orientation orientation, DiscreteCoordinates positionDiscreteCoordinates, double speed, int maxDistance) {
        super(area, orientation, positionDiscreteCoordinates, speed, maxDistance);
        handler = new ArrowInteractionHandler();
        createArrowSprite();
        arrowSprite = new Sprite(spriteName, 1, 1, this);
    }

    public void draw(Canvas canvas){
        arrowSprite.draw(canvas);
    }

    /**
     * choisi le sprite de la flèche selon la direction dans laquelle elle est tirée
     */

    public void createArrowSprite(){
        if (getOrientation() == Orientation.DOWN){
            spriteName = "icoop/arrowDown";
        }
        else if (getOrientation() == Orientation.LEFT){
            spriteName = "icoop/arrowLeft";
        }
        else if (getOrientation() == Orientation.RIGHT){
            spriteName = "icoop/arrowRight";
        }
        else {
            spriteName = "icoop/arrowUp";
        }
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

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class ArrowInteractionHandler implements ICoopInteractionVisitor {

        /**
         * permet aux flèches de faire détonner les explosifs
         */

        public void interactWith(Explosive explosive, boolean b){
            explosive.activate();
            stopProjectile();
        }


        public void interactWith(ICoopPlayer player, boolean b){
            player.attack(2, AttackType.PHYSICAL_ATTACK);
            stopProjectile();
        }

        /**
         * fait que les flèches ne blessent pas les autres ennemis
         */
        public void interactWith(Foe foe, boolean b){
            stopProjectile();
        }
    }
}