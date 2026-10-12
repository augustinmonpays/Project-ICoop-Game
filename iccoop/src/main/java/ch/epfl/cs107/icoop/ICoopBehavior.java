package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.actor.ElementalEntity;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.Unstoppable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

public class ICoopBehavior extends AreaBehavior {
    private ICoopCellType [][]allCells;
    public ICoopBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        allCells = new ICoopCellType[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICoopCellType color = ICoopCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICoopCell(x, y, color));
                allCells[y][x] = color;
            }
        }
    }

    public int getHeight() {return super.getHeight();}
    public int getWidth() {return super.getWidth();}
    public ICoopCellType getCellType(int height, int width) {
        return ((ICoopCell)getCell(height, width)).get();
    }

    public class ICoopCell extends Cell implements ICoopInteractionVisitor {

            private final ICoopCellType type;

            public ICoopCell(int x, int y, ICoopCellType type) {
                super(x, y);
                this.type = type;
            }

            public ICoopCellType get() {return type;}

            @Override
            protected boolean canLeave(Interactable entity) {
                return true;
            }


        /**
         * On vérifie a chaque fois pour s'assurer que deux Actors qui se comportent comme
         * des Elementals Entity (Sinon on fait pas le test), et qui ont des éléments différents
         * ne peuvent aller sur la même case.
         * On a aussi créer l'élément invisisble comme ca les murs deviennent invisible quand ils sont
         * désactivés donc on peut marcher dessus
         * @param entity (Interactable), not null
         * @return
         */
        @Override
            protected boolean canEnter(Interactable entity) {
                if(entity instanceof Unstoppable) {
                    return true;
                }
                for (Interactable ent : entities ){
                    if (ent.takeCellSpace()){
                        return false;
                    }if (ent instanceof ElementalEntity && entity instanceof ElementalEntity){
                        String element = ((ElementalEntity)ent).element();
                        String element2 = ((ElementalEntity)entity).element();
                        if (!element.equals(element2) && !element.equals("invisible") && !element2.equals("invisible")){
                            return false;
                        }
                    }
                }
                return type.canWalk;
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


    }


    public enum ICoopCellType{
        NULL(0, false, false),
        WALL(-16777216, false, false),
        IMPASSABLE(-8750470, false, true),
        INTERACT(-256, true, true),
        DOOR(-195580, true, true),
        WALKABLE(-1, true, true),
        ROCK(-16777204, true, true),
        OBSTACLE(-16723187, true, true)
        ;

        final int type;
        final boolean canWalk;
        final boolean canFly;

        ICoopCellType(int type, boolean isWalkable, boolean canFly) {
            this.type = type;
            this.canFly = canFly;
            this.canWalk = isWalkable;
        }

        public static ICoopCellType toType(int type) {
            for (ICoopCellType ict : ICoopCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }
}
