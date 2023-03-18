package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.game.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public interface GameController {

    public boolean getIsStarted();
    public void setIsStarted(boolean newState);
    public void start();
    public int getPlayerPoints();
    public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException;
    public int getPlayerTurn();
    public void moveTiles(ArrayList<Coordinates> source, ArrayList<Coordinates> destination);
    public ArrayList<Coordinates> getLivingRoomCoordinates();
    public ArrayList<ItemTile> getItemTiles(ArrayList<Coordinates> coo);
    public void refillLivingRoom();
    public boolean checkBookshelfComplete();
    public Player getWinner();
    public void endGame();



}
