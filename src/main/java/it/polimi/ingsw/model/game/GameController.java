package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public interface GameController {

    public boolean getState();
    public void setState(boolean newState);
    public void start();
    public int getPlayerPoints();
    public ArrayList<ScoringToken> getPlayerTokens(String player);
    public int getPlayerTurn();
    public void moveTile(ArrayList<Coordinates> source, ArrayList<Coordinates> destination);
    public ArrayList<Coordinates> getLivingRoomCoordinates();
    public ArrayList<ItemTile> getItemTiles(ArrayList<Coordinates> coo);
    public void refilLivingRoom();
    public boolean checkBookshelfComplete();
    public Player getWinner();
    public void endGame();



}
