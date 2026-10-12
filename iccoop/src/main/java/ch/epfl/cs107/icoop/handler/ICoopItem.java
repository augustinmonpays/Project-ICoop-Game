package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.InventoryItem;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;

public enum ICoopItem implements InventoryItem {

    /**
     * représente les différents objets que peuvent posséder les players
     * ils sont construits avec leur nom et le nom de leur sprite pour
     * simplifier leur création
     */

    SWORD("Sword", "icoop/sword.icon"),
    FIRE_KEY("FireKey", "icoop/key_red"),
    WATER_KEY("WaterKey", "icoop/key_blue"),
    FIRE_STAFF("FireStaff", "icoop/staff_fire.icon"),
    WATER_STAFF("WaterStaff", "icoop/staff_water.icon"),
    EXPLOSIVE("Explosive", "icoop/explosive");

    private String item;
    private String spriteName;

    ICoopItem(String object, String spriteName){
        item = object;
        this.spriteName = spriteName;
    }

    @Override
    public int getPocketId() {
        return 0;
    }

    @Override
    public String getName() {
        return  "" + item;
    }

    public String getSpriteName(){
        return spriteName;
    }
}