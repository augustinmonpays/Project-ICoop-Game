package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

import static ch.epfl.cs107.icoop.actor.Foe.ANIMATION_DURATION;

public class Stone extends Obstacle {
    private Logic isDestroyed = Logic.FALSE;
    private Sprite rockSprite;
    private Animation deathAnimation;

    /**
     * créer un nouveau type de rocher incassable par le joueur, les adversaires et les bombes
     */

    public Stone(Area owner, Orientation orientation, DiscreteCoordinates coordinates){
        super(owner, orientation, coordinates);
        rockSprite = new Sprite ("rock.3", 1, 1 , this);
        deathAnimation = new Animation("icoop/vanish", 7, 1, 1, this, 32, 32, ANIMATION_DURATION / 7, false);
    }

    public void draw(Canvas canvas){
        if(isDestroyed.isOn()){
            deathAnimation.draw(canvas);
        }else{
            rockSprite.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isDestroyed.isOn()){
            deathAnimation.update(deltaTime);
            if (deathAnimation.isCompleted()){
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    public void destroy(){
        isDestroyed = Logic.TRUE;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return super.getCurrentCells();
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    public boolean takeCellSpace(){
        return !isDestroyed.isOn();
    }
}
