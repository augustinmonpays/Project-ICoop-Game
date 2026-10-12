package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.Unstoppable;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class WaterFall extends AreaEntity implements Unstoppable {
    Animation waterFallAnimation;

    /**
     * créer un sprite de cascade quelque part sur la map
     */
    public WaterFall(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        waterFallAnimation = new Animation("icoop/waterfall", 3, 3, 3, this, 64, 64, 3, true);
    }

    public void update(float dt){
        waterFallAnimation.update(dt);
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of();
    }

    @Override
    public boolean takeCellSpace() {
        return false;
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
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        waterFallAnimation.draw(canvas);
    }
}
