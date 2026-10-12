package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

public class HealingPotion extends Potion {
    private HealingPotionInteractionHandler handler;


    public HealingPotion(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates, "icoop/fioleheal");
        this.handler = new HealingPotionInteractionHandler();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

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

    private class HealingPotionInteractionHandler implements ICoopInteractionVisitor {

        /**
         * elle cancel tous les effets visuels du player
         * @param player
         * @param isPlayerInteraction
         */
        @Override
        public void interactWith(ICoopPlayer player, boolean isPlayerInteraction) {
            if(!isUsed() && isCollected()){
                player.unstun();
                player.unquake();
            }
        }
    }


}
