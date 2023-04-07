package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.util.ArrayList;

public interface GameModelInterface {

    boolean getIsStarted();
    void start() throws NotAllPlayersHaveJoinedException, GameNotEndedException;
    String getPlayerInTurn() throws GameEndedException, GameNotStartedException;
    void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException, GameNotStartedException;
    boolean getItemTiles(ArrayList<Coordinates> coords) throws EmptySlotException;
    void refillLivingRoom();
    boolean checkBookshelfComplete();
    String getWinner() throws GameNotEndedException, GameNotStartedException;
    boolean checkRefill();
    void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException, TokenAlreadyGivenException;
    boolean setPlayerTurn();

     boolean canStart();
     // CHAT FUNCTIONALITIES
    ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException;

    void postMessage(String sender, String receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException;
    public void postMessage(String sender, String message) throws InvalidPlayerException;
}
