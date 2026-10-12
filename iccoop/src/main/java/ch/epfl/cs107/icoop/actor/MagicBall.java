package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class MagicBall extends Projectiles {
    private Animation magicBallAnimation;
    private final int damage = 1;
    private MagicBallInteractionHandler handler;
    private String element;
    private String spriteName;

    public MagicBall(Area area, Orientation orientation, DiscreteCoordinates positionDiscreteCoordinates, double speed, int maxDistance, String element) {
        super(area, orientation, positionDiscreteCoordinates, speed, maxDistance);
        this.element = element;
        if (element.equals("fire")){
            spriteName = "icoop/magicFireProjectile";
        }
        else if (element.equals("water")){
            spriteName = "icoop/magicWaterProjectile";
        }
        magicBallAnimation = new Animation(spriteName, 4, 1, 1, this, 32, 32, 4, true);
        handler = new MagicBallInteractionHandler();
    }

    public void update(float deltaTime){
        super.update(deltaTime);
        magicBallAnimation.update(deltaTime);

    }

    public void draw (Canvas canvas) {
        magicBallAnimation.draw(canvas);
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        super.acceptInteraction(v, isCellInteraction);
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

    private class MagicBallInteractionHandler implements ICoopInteractionVisitor {
        public void interactWith(Explosive explosive, boolean isBombInteraction){
            explosive.activate();
            stopProjectile();
        }

        public void interactWith(Foe foe, boolean isFoeInteraction){
            if (element.equals("fire")){
                foe.attack(damage, AttackType.FIRE_ATTACK);
            }
            else {
                foe.attack(damage, AttackType.WATER_ATTACK);
            }
            stopProjectile();
        }

        public void interactWith(Rocks rock, boolean isRockInteraction){
            rock.destroy();
            stopProjectile();
        }
    }
}