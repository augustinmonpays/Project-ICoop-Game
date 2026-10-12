package ch.epfl.cs107.icoop.Extensions;

import ch.epfl.cs107.icoop.actor.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Potion extends ICoopCollectable implements Interactor {
    private Sprite sprite;
    private Sprite actionSprite; //est dessiné quand il y a une action sur la potion
    private int durationOfStrikes;
    private Logic used = Logic.FALSE;

    public Potion(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        sprite = new Sprite(spriteName, 1.f, 1.f, this);
    }

    /**
     * malgré le fait que la classe est abstraite donc non instanciable, on lui fait une méthode draw
     * pour qu'elle puisse dessiner les sprites de fiole vide et les éclairs d'utilisation d'une fiole
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
       sprite.draw(canvas);
       if (actionSprite != null) {
           actionSprite.draw(canvas);
       }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        durationOfStrikes--;
        if (isCollected()){
            //Quand elle est utilisée on dessine un fiole vide
            this.sprite = new Sprite("icoop/fiolevide", 1.f, 1.f, this);

            /**
             * Comme quand un player a une immunity on dessine un fois sur deux les éclairs
             * pour avoir un effet éléectrique
             */
            if(durationOfStrikes > 0 && durationOfStrikes % 2 == 0){
                actionSprite = new Sprite("icoop/fiolestrike", 1.f, 1.f, this);
            }else{
                actionSprite = null;
            }
        }
        //On met 1 car la duration est initialisée a 0, ainsi on attend le moment ou elle est égal a 1
        //pour unregister l'Actor
        if (durationOfStrikes == 1) {
            getOwnerArea().unregisterActor(this);
        }
        }

    @Override
    public boolean takeCellSpace() {
        return false;
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
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Lorsqu'on va collect une potion on va activer le Logic used,
     * qui va faire en sorte que la fiole vide et que les éclairs soient dessinés
     */
    @Override
    public void collect() {
        super.collect();
        if(used.isOff()) {
            durationOfStrikes = 21;
            used = Logic.TRUE;
        }
    }

    public boolean isUsed(){return used.isOff();}
}
