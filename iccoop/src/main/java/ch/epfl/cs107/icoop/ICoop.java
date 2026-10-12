package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.Extensions.InvisibleActor;
import ch.epfl.cs107.icoop.Extensions.Sanctum;
import ch.epfl.cs107.icoop.Extensions.SanctumEntrance;
import ch.epfl.cs107.icoop.actor.CenterOfMass;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.*;
import ch.epfl.cs107.icoop.handler.ChallengeIsCompleted;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.PauseMenu;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ch.epfl.cs107.icoop.KeyBindings.*;
import static ch.epfl.cs107.icoop.area.ICoopArea.DEFAULT_SCALE_FACTOR;
import static java.lang.Math.*;


public class ICoop extends AreaGame implements DialogHandler, ChallengeIsCompleted {


    private final String[] areas = {"Spawn", "OrbWay", "Maze", "Arena", "SanctumEntrance", "Sanctum"};
    private ICoopPlayer player1;
    private ICoopPlayer player2;
    private int areaIndex;
    private CenterOfMass center;
    private Dialog dialog;
    private int zoomFactor;
    private int keyPressCounter;
    private Logic transitionEffect = Logic.FALSE;
    private float transitionScaleFactor;
    private float effectScaleFactor = DEFAULT_SCALE_FACTOR + zoomFactor; //c'est ce float qui fera varier le camera scale factor, il faut donc il débute a la même valeur que le scale factor courant du jeu
    private Logic stunningEffect = Logic.FALSE; // lorsqu'il est activé, l'effet se met en marche
    private Logic increaseFactor = Logic.FALSE; // ce Logic sert lors du stunning effect pour savoir si on va ajouter ou faire diminuer le scale factor
    private Logic quakeEffect = Logic.FALSE;
    private Map<String, ICoopArea> allAreas = new HashMap<String, ICoopArea>();;

    @Override
    public String getTitle() {
        return "ICoop";
    }

    private void createAreas() {

        ICoopArea spawn = new Spawn(this, this);
        ICoopArea orbWay = new OrbWay(this, this);
        ICoopArea maze = new Maze(this, this);
        ICoopArea arena = new Arena(this, this);
        ICoopArea sanctumEntrance = new SanctumEntrance(this);
        ICoopArea sanctum = new Sanctum(this);

        addArea(spawn);
        addArea(orbWay);
        addArea(maze);
        addArea(arena);
        addArea(sanctumEntrance);
        addArea(sanctum);

        allAreas.put("Spawn", spawn);
        allAreas.put("OrbWay", orbWay);
        allAreas.put("Maze", maze);
        allAreas.put("Arena", arena);
    }

    /**
     * @param window     (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        //on vérifie dans la méthode reset a chaque fois si on appuie pas sur la touche R ou T ou si un perso n'est pas
        //mort, pour lancer un reset
        reset();
        //On vérifie dans cette méthode si un des effets visuels est activé
        //si oui on fait l'effet visuel correspondant
        cameraScaleFactor();

        //On vérifie si les joueurs sont stun ou non pour lancer l'effet visuel correspondant
        if (player1.isStunned() || player2.isStunned()){
            stunningEffect = Logic.TRUE;
            player2.stun();
            player1.stun();
        }

        //On vérifie si les joueurs sont quaked ou non pour lancer l'effet visuel correspondant
        if(player1.isQuaked() || player2.isQuaked()){
            quakeEffect = Logic.TRUE;
            player2.quake();
            player1.quake();
        }


        //Pour update le dialogue quand on appuie sur espace
        if (dialog != null && !(dialog.isCompleted())) {
            Keyboard keyboard = getCurrentArea().getKeyboard();
            if (keyboard.get(KeyBindings.NEXT_DIALOG).isPressed()) {
                dialog.update(deltaTime);
            }
        } else {
            switchArea();
            super.update(deltaTime);
        }
        //Pour éviter le NullPointerExeption
        if (dialog != null && dialog.isCompleted()) {
            dialog = null;
        }
    }
    @Override
    public void end() {
    }

    @Override
    public void draw() {
        super.draw();
        if(dialog != null){
            dialog.draw(getWindow());
        }
    }

    /**
     * sets the area named `areaKey` as current area in the game Tuto2
     *
     * @param areaKey (String) title of an area
     */
    private void initArea(String areaKey) {
        ICoopArea area = (ICoopArea) setCurrentArea(areaKey, false);//Une liste des coordonées de Spawn des deux joueurs : d'abord ce de fire, puis de l'water
        ArrayList<DiscreteCoordinates> spawnPosition = area.getPlayerSpawnPosition();
        player1 = new ICoopPlayer(area, Orientation.DOWN, spawnPosition.get(0), "icoop/player", "fire", RED_PLAYER_KEY_BINDINGS);
        player1.enterArea(area, spawnPosition.get(0));
        player2 = new ICoopPlayer(area, Orientation.DOWN, spawnPosition.get(1), "icoop/player2", "water", BLUE_PLAYER_KEY_BINDINGS);
        player2.enterArea(area, spawnPosition.get(1));
        center = new CenterOfMass(player1, player2);
        getCurrentArea().setViewCandidate(center);
    }

    /**
     * switches from one area to the other
     * the player is healed when moving to a new area
     */
    private void switchArea() {
        if (player1.passingADoor()) {
            unregisterPlayers();
            ArrayList<DiscreteCoordinates> spawnPosition = player1.getDoorCoordinates();
            ICoopArea currentArea = (ICoopArea) setCurrentArea(player1.getArea(), false);
            registerPlayers(currentArea, spawnPosition);
            player1.setPassingADoor(false);
            if (distanceBetween(player1.getPosition(), player2.getPosition()) < 6) { // on vérifie qu'ils soient pas trop éloignés, sinon l'effet n'est pas bwater
                transitionEffect = Logic.TRUE;
                transitionScaleFactor = DEFAULT_SCALE_FACTOR + zoomFactor + 5;//on initialise ici a partir d'ou va partir le scale factor de transition, qui fera que baisser pour avoir un effet de zoom
            }                                                                  // le chiffre en plus c'est de combien on va zoomer pour revenir a la scale factor d'0rigine
        }
        else if (player2.passingADoor()) {
            unregisterPlayers();
            ArrayList<DiscreteCoordinates> spawnPosition = player2.getDoorCoordinates();
            ICoopArea currentArea = (ICoopArea) setCurrentArea(player2.getArea(), false);
            registerPlayers(currentArea, spawnPosition);
            player2.setPassingADoor(false);
            player1.setPassingADoor(false);
            if (distanceBetween(player1.getPosition(), player2.getPosition())< 6){ // on vérifie qu'ils soient pas trop éloignés, sinon l'effet n'est pas bwater
            transitionEffect = Logic.TRUE;
            transitionScaleFactor = DEFAULT_SCALE_FACTOR + zoomFactor + 5;
            }
        }
    }

    public void unregisterPlayers(){
        player1.leaveArea();
        player2.leaveArea();
    }

    public void registerPlayers(ICoopArea area, ArrayList<DiscreteCoordinates> spawnPosition){
        player1.enterArea(area, spawnPosition.get(0));
        player2.enterArea(area, spawnPosition.get(1));
        getCurrentArea().setViewCandidate(center);
    }

    private void reset(){

        Keyboard keyboard = getCurrentArea().getKeyboard();
        if (keyboard.get(RESET_GAME).isDown()){
            dialog = null;
            begin(getWindow(), getFileSystem());
            stunningEffect = Logic.FALSE;
            quakeEffect = Logic.FALSE;

        }
        if(keyboard.get(RESET_AREA).isDown()|| player1.getPlayerHP().isOff() || player2.getPlayerHP().isOff()){
            getCurrentArea().begin(getWindow(), getFileSystem());
            initArea(getCurrentArea().getTitle());
            stunningEffect = Logic.FALSE;
            quakeEffect = Logic.FALSE;
        }
    }

    /** On gère ici tout ce qui concerne le changement de caméra :
     * 1) le zoom quand on rentre dans une salle (Quand on est en "transition")
     * 2) l'effet de stun qui fait plein de chagement de caméras
     * */
    private void cameraScaleFactor() {

        //Quand il n'y a pas d'effet la seule option est de changer la caméra (deux choix de positions)
        //avec la touche C
        if (transitionEffect.isOff()) {
            Keyboard keyboard = getCurrentArea().getKeyboard();
            if (keyboard.get(Keyboard.C).isPressed()) {
                keyPressCounter++;
                switch (keyPressCounter) {
                    case 0:
                        zoomFactor = 0;
                        break;
                    case 1:
                        zoomFactor = 4;
                        keyPressCounter = -1;
                        break;  // C'est -1 comme ca quand on retourne dans la boucle la premiere instruction remet le counteur a 0. Chaque valeur que va prendre le compteur de fois ou on appuie sur C est associé a un zoom factor
                }

            }
            ((ICoopArea) getCurrentArea()).setCameraScaleFactor((float) (max(DEFAULT_SCALE_FACTOR + zoomFactor, DEFAULT_SCALE_FACTOR * 0.75 + zoomFactor + distanceBetween(player1.getPosition(), player2.getPosition()) / 2)));
        }

        if (transitionEffect.isOn()){

            transitionScaleFactor -= 0.1f * (transitionScaleFactor - (DEFAULT_SCALE_FACTOR + zoomFactor)); //la caméra s'approche de plus en plus de la valeur cible (DEFAULT_SCALE_FACTOR + zoomFactor). A chaque fois c'est 0.1f (donc 10%) de la valeur restante

            ((ICoopArea) getCurrentArea()).setCameraScaleFactor(transitionScaleFactor);

            if (transitionScaleFactor <= DEFAULT_SCALE_FACTOR + zoomFactor + 0.01f) { // ici on fait un plus 0.8 pour pas que le transiton scale factor reste
                transitionEffect = Logic.FALSE;
                ((ICoopArea) getCurrentArea()).setCameraScaleFactor(DEFAULT_SCALE_FACTOR + zoomFactor);
            }

        }
/**
 * ici sont gérés les effets du stunning effect sur la caméra
 */
        if (stunningEffect.isOn()){
            player2.hideInventory();
            player1.hideInventory();

            if (increaseFactor.isOn()){ //ici on cherche a faire des aller retour avec la caméra (ajouter ou enlever des valeur a scale factor)
                effectScaleFactor += 0.25f; //0.25 est une bonne valeur pour pas que l'effet soit trop violent
                if (effectScaleFactor >= 1.7 *DEFAULT_SCALE_FACTOR) {  // ici c'est la valeur que va prendre le scale factor quand elle sera le plus zoomé
                    increaseFactor = Logic.FALSE;
                }
            }else {
                effectScaleFactor -= 0.25f;
                if(effectScaleFactor <= 0.8*DEFAULT_SCALE_FACTOR){
                    increaseFactor = Logic.TRUE;
                }
            }

            ((ICoopArea) getCurrentArea()).setCameraScaleFactor(effectScaleFactor);


            if (player1.isUnStunned() || player2.isUnStunned()) {
                effectScaleFactor = DEFAULT_SCALE_FACTOR + zoomFactor;
                stunningEffect = Logic.FALSE;
                player1.unstun();
                player2.unstun();
                player1.drawInventory();
                player2.drawInventory();
            }
        }
        /**
         * ici on gère le quakeEffect (tremblement). On créé des invisibleActor qui sont des acteurs qu'on a
         * créé, qui sont uniquement caractérisés par leur vecteur position, et c'est la seule chose
         * dont a un besoin ici.
         * On leur associe la position du centre de masse actuel, plus un chiffre au hasard.
         * Ensuite on creer le centre de masse temporaire, avec ces deux acteurs qui sont positionnées
         * au hasard autour du centre de masse, et qui vont en creer un nouvwater temporaire, qui changera
         * a chaque boucle
         */
        if(quakeEffect.isOn()){

             Actor actor = new InvisibleActor(new Vector(center.getPosition().getX()+(float) random(),center.getPosition().getY() + (float) random()));
             Actor actor2 = new InvisibleActor(new Vector(center.getPosition().getX()+(float) random(),center.getPosition().getY() + (float) random()));
             CenterOfMass temporaryCenterOfMass = new CenterOfMass(actor,actor2);

             ((ICoopArea) getCurrentArea()).setViewCandidate(temporaryCenterOfMass);

            if (player1.isUnQuaked() || player2.isUnQuaked()) {
                effectScaleFactor = DEFAULT_SCALE_FACTOR + zoomFactor;
                quakeEffect = Logic.FALSE;
                player1.unquake();
                player2.unquake();
                player1.drawInventory();
                player2.drawInventory();
                getCurrentArea().setViewCandidate(center);
            }

        }


    }

    /**
     * ça calcule la distance entre deux vectors
     * elle est en publique car on l'utilise a d'autres endroits
     * @param positionPlayer1
     * @param positionPlayer2
     * @return
     */
    public double distanceBetween(Vector positionPlayer1, Vector positionPlayer2){
        return positionPlayer1.sub(positionPlayer2).getLength();
    }

    @Override
    public void publish(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean isCompleted() {
        int i = 0;
        for(String s : allAreas.keySet()){
            if(s!="Spawn"){
                if(allAreas.get(s).isOn()){i++;}
            }

        }
        return (i == allAreas.size()-1);
    }
}
