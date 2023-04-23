package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.remoteInterfaces.BookshelfSubscriber;
import it.polimi.ingsw.remoteInterfaces.ChatSubscriber;
import it.polimi.ingsw.remoteInterfaces.PlayerSubscriber;

import java.util.ArrayList;

public interface GameModelInterface {

    boolean getIsStarted();

    void subscribeToListener(BoardSubscriber subscriber);

    void subscribeToListener(BookshelfSubscriber subscriber);

    void subscribeToListener(ChatSubscriber subscriber);

    void subscribeToListener(PlayerSubscriber subscriber);
    void start() throws NotAllPlayersHaveJoinedException, GameNotEndedException;
    String getPlayerInTurn() throws GameEndedException, GameNotStartedException;
    void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException, GameNotStartedException;
    boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException;
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
    void postMessage(String sender, String message) throws InvalidPlayerException;
}
