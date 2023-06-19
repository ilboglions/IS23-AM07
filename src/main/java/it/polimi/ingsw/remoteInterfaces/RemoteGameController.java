package it.polimi.ingsw.remoteInterfaces;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.*;
import java.util.ArrayList;

/**
 * This interface represents the methods exposed by the GameController over RMI
 */
public interface RemoteGameController extends Remote{

    /**
     * first method to call, it is used to confirm that a certain set of item tiles can be taken by the player in the board (but without actually taking it)
     * @param player the player that perform the action
     * @param coords the list of coordinates where tiles should have been taken
     * @return true, if the action is permitted
     * @throws RemoteException RMI Exception
     * @throws PlayerNotInTurnException this player is not the player in turn
     * @throws GameNotStartedException the game has not started, yet
     * @throws GameEndedException the game has already ended
     * @throws EmptySlotException one of the coordinates refers to an empty slot
     */
    boolean checkValidRetrieve(String player, ArrayList<Coordinates> coords) throws RemoteException, PlayerNotInTurnException, GameNotStartedException, GameEndedException, EmptySlotException;
    /**
     * the second method to be called by the player, it performs the insertion of the tile in the bookshelf of the player, removing them from the board.
     * it also checks if the livingroom should be refilled and, in that case invoke the refill action. If the player with this move completed his bookshelf
     * we check if he is the first one to do so, if that happens the player gets the FIRSTPLAYER token. At the end of the method the turn passes to the next player
     * @param player the player that is performing the action
     * @param source the coordinates to be taken
     * @param column the column where the coordinates will be inserted
     * @throws RemoteException RMI Exception
     * @throws GameNotStartedException the game has not started, yet
     * @throws GameEndedException the game has already ended
     * @throws NotEnoughSpaceException the column  selected has no space for the selected tiles
     * @throws PlayerNotInTurnException this player is not the player in turn
     * @throws EmptySlotException one of the coordinates refers to an empty slot
     * @throws InvalidCoordinatesException one of the coordinates is not valid
     */
    void moveTiles(String player, ArrayList<Coordinates> source, int column) throws RemoteException, GameNotStartedException, GameEndedException, NotEnoughSpaceException, PlayerNotInTurnException, EmptySlotException, InvalidCoordinatesException;
    /**
     * This method post a chat message to all the players
     * @param player username of the player that sent the message
     * @param message content of the message
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException the username of the sender is not valid
     */
    void postBroadCastMessage(String player, String message) throws RemoteException, InvalidPlayerException;
    /**
     * used to create a message to a certain player in the same game
     * @param player   the player that want to send the message
     * @param receiver the player that will receive the message
     * @param message  the message to be sent
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException if there isn't any player with that username inside the game
     * @throws SenderEqualsRecipientException if the string player and receiver are the same
     */
    void postDirectMessage(String player, String receiver, String message) throws RemoteException, InvalidPlayerException, SenderEqualsRecipientException;
    /**
     * used to subscribe a BoardSubscriber to the listeners
     * @param subscriber a BoardSubscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(BoardSubscriber subscriber) throws RemoteException;
    /**
     * used to subscribe a BookshelfSubscriber to the listeners
     * @param subscriber a BookshelfSubscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(BookshelfSubscriber subscriber) throws RemoteException;
    /**
     * used to subscribe a ChatSubscriber to the listeners
     * @param subscriber a ChatSubscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(ChatSubscriber subscriber) throws RemoteException;
    /**
     * used to subscribe a PlayerSubscriber to the listeners
     * @param subscriber a PlayerSubscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(PlayerSubscriber subscriber) throws RemoteException;
    /**
     * used to subscribe a gameSubscriber to the listeners
     * @param subscriber a game subscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(GameSubscriber subscriber) throws RemoteException;
    /**
     * used to subscribe a gameStateSubscriber to the listeners
     * @param subscriber a gameState subscriber
     * @throws RemoteException RMI Exception
     */
    void subscribeToListener(GameStateSubscriber subscriber) throws RemoteException;
    /**
     * handle the crash of a player
     * @param username the username of the player that is crashed
     * @throws PlayerNotFoundException if the player crashed is not found in the game
     * @throws RemoteException RMI Exception
     */
    void handleCrashedPlayer(String username) throws RemoteException, PlayerNotFoundException;
    /**
     * handle the case for which the player rejoin the game after he is crashed
     * @param username the username of the player
     * @throws PlayerNotFoundException if the player crashed is not found in the game
     * @throws RemoteException RMI Exception
     */
    void handleRejoinedPlayer(String username) throws RemoteException, PlayerNotFoundException;
    /**
     * triggers the HeartBeat in rmi connections
     * @param username the username of the player
     * @throws RemoteException RMI Exception
     */
    void triggerHeartBeat(String username) throws RemoteException;
    /**
     * Method used to trigger all the listeners when a player joins or re-joins a game after a crash, to receive the complete status of the game such as players bookshelf's, points or livingRoomBoard
     * @param userToBeUpdated the username of the user that needs to receive the updates
     * @throws RemoteException RMI Exception
     */
    void triggerAllListeners(String userToBeUpdated) throws RemoteException;
}
