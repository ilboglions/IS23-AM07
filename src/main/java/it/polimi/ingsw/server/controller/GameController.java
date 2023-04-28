package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.remoteInterfaces.BookshelfSubscriber;
import it.polimi.ingsw.remoteInterfaces.ChatSubscriber;
import it.polimi.ingsw.remoteInterfaces.PlayerSubscriber;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * the game controller ensures the communication through client controller and server model
 */
public class GameController extends UnicastRemoteObject implements RemoteGameController {
    @Serial
    private static final long serialVersionUID = -385148265230540371L;

    private final GameModelInterface gameModel;
    private final Set<Coordinates> selectedTiles;
    private final Object gameLock, chatLock;
    /**
     * creates the gameController
     *
     * @param gameModel the model reffered to the game
     *
     */
    public GameController(GameModelInterface gameModel) throws RemoteException {
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
     */
    public boolean checkValidRetrieve(String player, ArrayList<Coordinates> coords) throws RemoteException  {
        synchronized (gameLock) {
            try {
                if(!player.equals(gameModel.getPlayerInTurn())) return false;
            } catch (GameEndedException e) {
                return false;
            } catch (GameNotStartedException e) {
                return false;
            }

            try{
                if( gameModel.checkValidRetrieve(coords)){
                    selectedTiles.clear();
                    selectedTiles.addAll(coords);
                    return true;
                }
                return false;

            } catch (EmptySlotException e){
                return false;
            }
        }
    }

    /**
     * the second method to be called by the player, it performs the insertion of the tile in the bookshelf of the player, removing them from the board.
     * it also checks if the livingroom should be refilled and, in that case invoke the refill action. It also passes the turn to the next player
     * @param player the player that is performing the action
     * @param source the coordinates to be taken
     * @param column the column where the coordinates will be inserted
     * @return true, if the action has been correctly performed, false otherwise
     */
    public boolean moveTiles( String player, ArrayList<Coordinates> source, int column) throws RemoteException{
        synchronized (gameLock) {
            try {
                if(!player.equals(gameModel.getPlayerInTurn())) return false;
            } catch (GameEndedException e) {
                return false;
            } catch (GameNotStartedException e) {
                return false;
            }

            if(!this.selectedTiles.containsAll(source) || this.selectedTiles.size() != source.size()) return false;
            try {
                gameModel.moveTiles(source,column);
                if (gameModel.checkRefill()){
                    gameModel.refillLivingRoom();
                }

                gameModel.updatePlayerPoints(player);

                gameModel.checkBookshelfComplete();

                gameModel.setPlayerTurn();


                return true;
            } catch (InvalidCoordinatesException | EmptySlotException | NotEnoughSpaceException |
                     InvalidPlayerException | TokenAlreadyGivenException | GameNotStartedException e) {
                return false;
            }
        }
    }


    /**
     * used to create a broadcast message
     * @param player the player that will be post the message
     * @param message the message to be posted
     * @return true, if the message can be posted, false otherwise
     */
    public boolean postBroadCastMessage(String player, String message) throws RemoteException{
        synchronized (chatLock) {
            try {
                gameModel.postMessage(player, message);
            } catch (InvalidPlayerException e) {
                return false;
            }

            return true;
        }
    }

    /**
     * used to create a message to a certain player in the same game
     * @param player the player that want to send the message
     * @param receiver the player that will receive the message
     * @param message the message to be sent
     * @return false, if the message cannot be posted or sent to the receiver, true otherwise
     */
    public boolean postDirectMessage(String player, String receiver,String message) throws RemoteException{
        synchronized (chatLock) {
            try {
                gameModel.postMessage(player,receiver,message);
            } catch (SenderEqualsRecipientException e) {
                return false;
            } catch (InvalidPlayerException e) {
                return false;
            }

            return true;
        }
    }

    @Override
    public void subscribeToListener(BoardSubscriber subscriber) throws RemoteException {
        gameModel.subscribeToListener(subscriber);
    }

    @Override
    public void subscribeToListener(BookshelfSubscriber subscriber) throws RemoteException {
        gameModel.subscribeToListener(subscriber);
    }

    @Override
    public void subscribeToListener(ChatSubscriber subscriber) throws RemoteException {
        gameModel.subscribeToListener(subscriber);
    }

    @Override
    public void subscribeToListener(PlayerSubscriber subscriber) {
        gameModel.subscribeToListener(subscriber);
    }

    protected GameModelInterface getGameControlled(){
        return gameModel;
    }

    public void handleRejoinedPlayer(String username) throws PlayerNotFoundException, RemoteException {
        gameModel.handleRejoinedPlayer(username);
    }

    public void handleCrashedPlayer(String username) throws PlayerNotFoundException, RemoteException{
        gameModel.handleCrashedPlayer(username);
    }

}
