package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.EmptySlotException;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public interface GameController {

    public boolean getIsStarted();
    public void setIsStarted(boolean newState);
    public void start();
    public int getPlayerPoints(String player) throws InvalidPlayerException;
    public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException;
    public int getPlayerTurn();
    public void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException;

    public ArrayList<Coordinates> getLivingRoomCoordinates();
    public ArrayList<ItemTile> getItemTiles(ArrayList<Coordinates> coo);
    public void refillLivingRoom();
    public boolean checkBookshelfComplete();
    public Player getWinner();
    public void endGame();



}
