package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Orb extends ElementalItem {
    private OrbType orbType;
    final int ANIMATION_DURATION = 24;
    final int ANIMATION_FRAMES = 6;
    private Animation orbAnimation;
    private DialogHandler dialogHandler;


    public Orb (Area area, Orientation orientation, DiscreteCoordinates position, String element, DialogHandler dialogHandler) {
        super(area, orientation, position, element);
        if (element.equals("water")){
            orbType = OrbType.WATERORB;
        }
        else {
            orbType = OrbType.FIREORB;
        }

        final Sprite[] sprites = new Sprite[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            sprites[i] = new RPGSprite("icoop/orb", 1, 1, this,
                    new RegionOfInterest(i * 32, orbType.getSpriteYDelta(), 32, 32));
        }

        this.orbAnimation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES, sprites);
        this.dialogHandler = dialogHandler;
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        orbAnimation.update(deltaTime);
        if (isCollected()){
            getOwnerArea().unregisterActor(this);
            dialogHandler.publish(orbType.getDialogOfOrb());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        orbAnimation.draw(canvas);
    }

    /**
     * Chaque Orb est différenciée par le dialogue qu'elle émet et un int
     * "spriteYdelta" qui va différencier leur animation
     */
    private enum OrbType{
        WATERORB (new Dialog("orb_water_msg"), 0),
        FIREORB ( new Dialog("orb_fire_msg"), 64);


        private Dialog dialogOfOrb;
        private int spriteYdelta;


        OrbType(Dialog dialogOfOrb, int spriteYdelta ){
            this.dialogOfOrb = dialogOfOrb;
            this.spriteYdelta = spriteYdelta;
        }

        private int getSpriteYDelta(){
            return spriteYdelta;
        }

        private Dialog getDialogOfOrb(){
            return dialogOfOrb;
        }
    }
}
