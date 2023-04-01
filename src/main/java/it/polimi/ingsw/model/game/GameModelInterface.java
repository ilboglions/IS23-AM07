package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.util.ArrayList;

public interface GameModelInterface {

    boolean getIsStarted();
    void start() throws NotAllPlayersHaveJoinedException;
    String getPlayerInTurn() throws GameEndedException;
    void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException;
    boolean getItemTiles(ArrayList<Coordinates> coords) throws EmptySlotException;
    void refillLivingRoom();
    boolean checkBookshelfComplete();
    String getWinner() throws GameNotEndedException;
    boolean checkRefill();
    public void updatePlayerPoints(String username) throws InvalidPlayerException;
    public boolean setPlayerTurn();
}
