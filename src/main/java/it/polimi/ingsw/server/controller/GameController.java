package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.game.GameModelInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static it.polimi.ingsw.server.ServerMain.logger;

/**
 * the game controller ensures the communication through client controller and server model
 */
public class GameController extends UnicastRemoteObject implements RemoteGameController {

    private final GameModelInterface gameModel;
    /**
     * a set of selected tiles, updated foreach turn of the player
     */
    private final Set<Coordinates> selectedTiles;
    /**
     * locks used for thread synchronization
     */
    private final Object gameLock, chatLock;

    /**
     * the timers used in case of rmi connection
     */
    private final Map<String,ReschedulableTimer> timers;
    /**
     * the timer delay
     */
    private final long timerDelay = 15000;

    /**
     * creates the gameController
     * @param gameModel the model reffered to the game
     * @throws RemoteException RMI Exception
     */
    public GameController(GameModelInterface gameModel) throws RemoteException {
        this.timers = new HashMap<>();
        this.gameModel = gameModel;
        this.selectedTiles = new HashSet<>();
        this.gameLock = new Object();
        this.chatLock = new Object();
    }

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
    public boolean checkValidRetrieve(String player, ArrayList<Coordinates> coords) throws RemoteException, PlayerNotInTurnException, GameNotStartedException, GameEndedException, EmptySlotException {
        this.rescheduleTimer(player);
        synchronized (gameLock) {
            if(!player.equals(gameModel.getPlayerInTurn())) throw new PlayerNotInTurnException();

            if( gameModel.checkValidRetrieve(coords)){
                selectedTiles.clear();
                selectedTiles.addAll(coords);
                return true;
            }
            return false;
        }
    }

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
    public void moveTiles(String player, ArrayList<Coordinates> source, int column) throws RemoteException, GameNotStartedException, GameEndedException, NotEnoughSpaceException, PlayerNotInTurnException, EmptySlotException, InvalidCoordinatesException {
        this.rescheduleTimer(player);
        synchronized (gameLock) {
            if(!player.equals(gameModel.getPlayerInTurn())) throw new PlayerNotInTurnException();

            if(!this.selectedTiles.containsAll(source) || this.selectedTiles.size() != source.size()) throw new InvalidCoordinatesException("the selected tiles don't match!");

            gameModel.moveTiles(source,column);

            if (gameModel.checkRefill()){
                gameModel.refillLivingRoom();
            }
            gameModel.checkBookshelfComplete();
            try {
                gameModel.updatePlayerPoints(player);
            } catch (InvalidPlayerException | TokenAlreadyGivenException ignored) {}
            gameModel.setPlayerTurn();
        }
    }


    private void rescheduleTimer(String player) {
        synchronized (timers) {
            if (this.timers.get(player) != null && this.timers.get(player).isScheduled()) {
                this.timers.get(player).reschedule(timerDelay);
            }
        }
    }


    /**
     * This method post a chat message to all the players
     * @param player username of the player that sent the message
     * @param message content of the message
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException the username of the sender is not valid
     */
    public void postBroadCastMessage(String player, String message) throws RemoteException, InvalidPlayerException {
        synchronized (chatLock) {
            gameModel.postMessage(player, message);
        }
    }

    /**
     * used to create a message to a certain player in the same game
     * @param player   the player that want to send the message
     * @param receiver the player that will receive the message
     * @param message  the message to be sent
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException if there isn't any player with that username inside the game
     * @throws SenderEqualsRecipientException if the string player and receiver are the same
     */
    public void postDirectMessage(String player, String receiver, String message) throws RemoteException, InvalidPlayerException, SenderEqualsRecipientException {
        synchronized (chatLock) {
            this.rescheduleTimer(player);
            gameModel.postMessage(player,receiver,message);
        }
    }

    /**
     * used to subscribe a BoardSubscriber to the listeners
     * @param subscriber a BoardSubscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(BoardSubscriber subscriber) throws RemoteException {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * used to subscribe a BookshelfSubscriber to the listeners
     * @param subscriber a BookshelfSubscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(BookshelfSubscriber subscriber) throws RemoteException {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * used to subscribe a ChatSubscriber to the listeners
     * @param subscriber a ChatSubscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(ChatSubscriber subscriber) throws RemoteException {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * used to subscribe a PlayerSubscriber to the listeners
     * @param subscriber a PlayerSubscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(PlayerSubscriber subscriber) throws RemoteException
    {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * used to subscribe a gameSubscriber to the listeners
     * @param subscriber a game subscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(GameSubscriber subscriber) throws RemoteException
    {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * used to subscribe a gameStateSubscriber to the listeners
     * @param subscriber a gameState subscriber
     * @throws RemoteException RMI Exception
     */
    @Override
    public void subscribeToListener(GameStateSubscriber subscriber) throws RemoteException {
        synchronized (gameLock) {
            gameModel.subscribeToListener(subscriber);
        }
    }

    /**
     * protected method that gets the game controlled, used by the lobby controller
     * @return the game controlled
     */
    protected GameModelInterface getGameControlled(){
        return gameModel;
    }

    /**
     * handle the case for which the player rejoin the game after he is crashed
     * @param username the username of the player
     * @throws PlayerNotFoundException if the player crashed is not found in the game
     * @throws RemoteException RMI Exception
     */
    public void handleRejoinedPlayer(String username) throws PlayerNotFoundException, RemoteException {
        synchronized (gameLock) {
            gameModel.handleRejoinedPlayer(username);
        }
    }

    /**
     * handle the crash of a player
     * @param username the username of the player that is crashed
     * @throws PlayerNotFoundException if the player crashed is not found in the game
     * @throws RemoteException RMI Exception
     */
    public void handleCrashedPlayer(String username) throws PlayerNotFoundException, RemoteException {
        synchronized (gameLock) {
            gameModel.handleCrashedPlayer(username);
            if (timers.containsKey(username))
                this.stopTimer(username);

            try {
                if (gameModel.isRunning() && gameModel.getPlayerInTurn().equals(username)) {
                    this.selectedTiles.clear();
                    gameModel.setPlayerTurn();
                }
            } catch (GameNotStartedException | GameEndedException ignored) {
            }
        }
    }


    /**
     * private method used for initialize the timer of the player
     * @param username the username of the player
     */
    private void initializeTimer(String username){
        synchronized (timers) {
            this.timers.put(username, new ReschedulableTimer());
            this.timers.get(username).schedule(() -> {
                try {
                    this.handleCrashedPlayer(username);
                } catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }, this.timerDelay);
        }
    }

    /**
     * stops the timer of the lobby for the player with the given username
     * @param username the username of the player
     */
    private void stopTimer(String username){
        synchronized (timers) {
            this.timers.get(username).cancel();
            this.timers.remove(username);
        }
    }

    /**
     * triggers the HeartBeat in rmi connections
     * @param username the username of the player
     * @throws RemoteException RMI Exception
     */
    public void triggerHeartBeat(String username) throws RemoteException{
        synchronized (timers){
            logger.info("HEARTBEAT RECEIVED BY "+username);
            if(this.timers.get(username) == null || !this.timers.get(username).isScheduled()){
                this.initializeTimer(username);
                return;
            }
            this.timers.get(username).reschedule(this.timerDelay);
        }
    }

    /**
     * Method used to trigger all the listeners when a player joins or re-joins a game after a crash, to receive the complete status of the game such as players bookshelf's, points or livingRoomBoard
     * @param userToBeUpdated the username of the user that needs to receive the updates
     * @throws RemoteException RMI Exception
     */
    public void triggerAllListeners(String userToBeUpdated) throws RemoteException{
        synchronized (gameLock) {
            gameModel.triggerAllListeners(userToBeUpdated);
        }
    }

    /**
     * This method returns the set of the tiles selected
     * @return a set that contains all the selected Tiles
     */
    public Set<Coordinates> getSelectedTiles() {
        return (new HashSet<>(selectedTiles));
    }

    protected Map<String, ReschedulableTimer> getTimers() {
        return timers;
    }

}
