package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public interface GameModelInterface {

    public boolean getIsStarted();
    public void start() throws NotAllPlayersHaveJoinedException;
    public int getPlayerPoints(String player) throws InvalidPlayerException;
    public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException;
    public String getPlayerInTurn();
    public void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException;
    public ArrayList<ItemTile> getItemTiles(ArrayList<Coordinates> coo);
    public void refillLivingRoom();
    public boolean checkBookshelfComplete();
    public String getWinner() throws GameNotEndedException;
    public boolean nextPlayerTurn();



}
