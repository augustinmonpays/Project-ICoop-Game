package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;

public class ICoopInventory extends Inventory implements Inventory.Holder {

    public ICoopInventory(){
        super("inventory");
    }

    @Override
    public boolean possess(InventoryItem item) { //pas sur que l'implementation soit utile/nécessaire
        return false;
    }
}
