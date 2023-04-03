package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.util.ArrayList;
import java.util.Optional;

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
    void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException, TokenAlreadyGivenException;
    boolean setPlayerTurn();

     boolean canStart();
     // CHAT FUNCTIONALITIES
    ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException;

    void postMessage(String sender, Optional<String> reciver, String message) throws SenderEqualsRecipientException, InvalidPlayerException;
}
