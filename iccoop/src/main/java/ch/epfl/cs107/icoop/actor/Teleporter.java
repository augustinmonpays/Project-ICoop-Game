package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public class Teleporter extends Door{
    private Sprite spriteTeleporter;
    private Logic isOpened;
    private Key key1;
    private Key key2;

    public Teleporter(String aireDarrivee, Logic signal, ArrayList<DiscreteCoordinates> coordoneeDarrivee, Area ownerArea, DiscreteCoordinates currentMainCellCoordinates, Orientation orientation, Key key1, Key key2) {
        super(aireDarrivee, signal, coordoneeDarrivee, ownerArea, currentMainCellCoordinates, orientation);
        this.spriteTeleporter = new RPGSprite("shadow", 1, 1, this, new RegionOfInterest(0, 0, 32,
                32));
        this.key1 = key1;
        this.key2 = key2;
        this.isOpened = new And(key1, key2);
    }

    /**
     *dessine le téléporteur seulement si les deux clés ont été collectées
     */
    @Override
    public void draw(Canvas canvas) {
        if(isOpened.isOn()){
        spriteTeleporter.draw(canvas);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public boolean isCellInteractable(){
        return isOpened.isOn();
    }
}
