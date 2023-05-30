package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import java.util.ArrayList;

public interface GameModelInterface {

    boolean isStarted();
    void subscribeToListener(BoardSubscriber subscriber);
    void subscribeToListener(BookshelfSubscriber subscriber);
    void subscribeToListener(ChatSubscriber subscriber);
    void subscribeToListener(PlayerSubscriber subscriber);
    void subscribeToListener(GameSubscriber subscriber);
    void start() throws NotAllPlayersHaveJoinedException, GameNotEndedException;
    String getPlayerInTurn() throws GameEndedException, GameNotStartedException;
    void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException, GameNotStartedException;
    boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException;
    void refillLivingRoom();
    boolean checkBookshelfComplete();
    String getWinner() throws GameNotEndedException, GameNotStartedException;
    boolean checkRefill();
    void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException, TokenAlreadyGivenException, GameNotStartedException;
    boolean setPlayerTurn();

    void subscriberToListener(GameStateSubscriber subscriber);

    boolean canStart();
     // CHAT FUNCTIONALITIES
    ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException;
    void postMessage(String sender, String receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException;
    void postMessage(String sender, String message) throws InvalidPlayerException;
    void handleRejoinedPlayer(String username) throws PlayerNotFoundException;
    void handleCrashedPlayer(String username) throws PlayerNotFoundException;
    boolean isCrashedPlayer(String player);
    void triggerAllListeners(String userToBeUpdated);
}
