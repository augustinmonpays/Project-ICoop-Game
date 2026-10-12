package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Rocks;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public abstract class ICoopArea extends Area implements Logic {
        public final static float DEFAULT_SCALE_FACTOR = 12f;
        private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
        /**
         * Area specific callback to initialise the instance
         */
        protected abstract void createArea();

        /**
         * @return the player's spawn position in the area
         */
        public abstract ArrayList<DiscreteCoordinates> getPlayerSpawnPosition();

        /**
         * Callback to initialise the instance of the area
         * Ici, pour chaque Area on va vérifier le type de chaque de ses cellules.
         * Si c'est un OBSTACLE, ROCKS... En fonction de ça on va register l'actor correspondant sur la celulle.
         * @param window (Window): display context. Not null
         * @param fileSystem (FileSystem): given file system. Not null
         * @return true if the area is instantiated correctly, false otherwise
         */
        @Override
        public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            ICoopBehavior behavior = new ICoopBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            int height = behavior.getHeight();
            int width = behavior.getWidth();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    ICoopBehavior.ICoopCellType cellType = behavior.getCellType(x,y);
                    switch(cellType){
                        case ROCK:
                            registerActor(new Rocks(this, Orientation.DOWN, new DiscreteCoordinates(x,y)));
                            break;
                        case OBSTACLE:
                            registerActor(new Obstacle(this, Orientation.DOWN, new DiscreteCoordinates(x,y)));
                            break;
                    }
                }
            }
            return true;
        }
        return false;
    }

        @Override
        public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }



    @Override
    public boolean isViewCentered(){
        return false;
    }

    public void setCameraScaleFactor(float cameraScaleFactor) {
            this.cameraScaleFactor = cameraScaleFactor;
    }


}

