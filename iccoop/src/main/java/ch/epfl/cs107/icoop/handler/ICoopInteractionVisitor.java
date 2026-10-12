package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.Extensions.*;
import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * InteractionVisitor for the ICoop entities
 */

public interface ICoopInteractionVisitor extends AreaInteractionVisitor {
    /// Add Interaction method with all non Abstract Interactable
        default void interactWith(ICoopBehavior.ICoopCell cell, boolean isCellInteraction){}

        default void interactWith(ICoopPlayer player, boolean isPlayerInteraction){}

        default void interactWith(Door door, boolean isDoorInteraction){}

        default void interactWith(Explosive explosive, boolean isBombInteraction){}

        default void interactWith(Rocks rock, boolean isRockInteraction){}

        default void interactWith(Orb orb, boolean isOrbInteraction){}

        default void interactWith(Heart heart, boolean isHeartInteraction){}

        default void interactWith(ElementalWall elementalWall, boolean isElementalWall){}

        default void interactWith(PressurePlate plate, boolean isPlateInteraction){}

        default void interactWith(Foe foe, boolean isFoeInteraction){}

        default void interactWith(Fire fire, boolean isFireInteraction){}

        default void interactWith(StunningPotion stunningPotion, boolean isStunningPotionInteraction){}

        default void interactWith(HealingPotion healingPotion, boolean isHealingPotionInteraction){}

        default void interactWith(Bier bier, boolean isBierInteraction){}

        default void interactWith(QuakePotion quakePotion, boolean isQuakePotionInteraction){}

        default void interactWith(BombFoe bombFoe, boolean isBombFoeInteraction) {}

        default void interactWith(FinalDoor finalDoor, boolean isFinalDoorInteraction) {}

        default void interactWith(Staff staff, boolean isStaffInteraction){}

        default void interactWith(MagicBall magicBall, boolean isMagicBallInteraction){}

        default void interactWith(Key key, boolean isKeyInteraction){}

        default void interactWith(Teleporter teleporter, boolean isTeleporterInteraction){}

        default void interactWith(Lever lever, boolean isLeverInteraction){}

        default void interactWith(Chest chest, boolean isChestInteraction){}

        default void interactWith(SanctumDoor sanctumDoor, boolean isSanctumDoorInteraction){}

        default void interactWith(LittleBoy littleBoy, boolean isLittleBoyInteraction){}

        default void interactWith(Stone stone, boolean isStoneInteraction){}
}
