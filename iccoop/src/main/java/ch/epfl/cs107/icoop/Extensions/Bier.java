package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

public class Bier extends Potion{
    private DialogHandler dialogHandler;
    private BierInteractionHandler handler;


    public Bier(Area owner, Orientation orientation, DiscreteCoordinates coordinates, DialogHandler dialogHandler) {
        super(owner, orientation, coordinates, "icoop/bière");
        this.handler = new BierInteractionHandler();
        this.dialogHandler = dialogHandler;
    }

    /**
     * quand elle est ramassée elle émet un dialogue et diaparait
     * @param dt elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update (float dt){
        super.update(dt);
        if (isCollected()){
            getOwnerArea().unregisterActor(this);
            dialogHandler.publish(new Dialog("drink"));
        }
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);}

        @Override
        public List<DiscreteCoordinates> getFieldOfViewCells() {
            return List.of();
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

        private class BierInteractionHandler implements ICoopInteractionVisitor {
            @Override
            public void interactWith(ICoopPlayer player, boolean isPlayerInteraction) {
                if(!isUsed()){
                    player.stun();
                }
            }
        }


    }


