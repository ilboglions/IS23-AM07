package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.lobby.Lobby;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.remoteInterfaces.RemoteLobbyController;

import java.util.HashMap;
import java.util.Map;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Optional;

/**
 * the lobby controller ensures the communication through client controller and server model
 */
public class LobbyController extends UnicastRemoteObject implements RemoteLobbyController {


    /**
     * the reference to the lobby model
     */
    private final Lobby lobbyModel;
    /**
     * the map containing the gameControllers
     */
    private final Map<GameModelInterface,GameController> gameControllers;
    /*
        lobbyLock is used to synchronize the methods of LobbyController. it is equivalent to use synchronize in the method's signature
     */

    /**
     * the lock for synchronize the threads
     */
    private final Object lobbyLock;
    /**
     * the map representing the timers for the players
     */
    private final Map<String, ReschedulableTimer> timers;
    private final long timerDelay = 15000;

    /**
     * creates the controller of the lobby
     * @param lobbyModel the model of the lobby
     */
    public LobbyController(Lobby lobbyModel) throws RemoteException {
        super();
        this.timers = new HashMap<>();
        this.lobbyModel = lobbyModel;
        gameControllers = new HashMap<>();
        lobbyLock = new Object();
    }

    /**
     * make it possible for the player to join the lobby e select a game
     *
     * @param player the username of the player to be added
     * @return a RemoteGameController, if the player was crashed, an empty optional if the player is correctly logged in the  lobby
     * @throws NicknameAlreadyUsedException if the player is already inside a game
     * @throws RemoteException in case of a network error occurs
     */
    public Optional<RemoteGameController> enterInLobby(String player) throws RemoteException, NicknameAlreadyUsedException {
        synchronized (lobbyLock) {
            try {
                lobbyModel.createPlayer(player);
                return Optional.empty();
            } catch (NicknameAlreadyUsedException e) {

                /* search if the player is crashed in a game */
                for( Map.Entry<GameModelInterface, GameController> entry : gameControllers.entrySet()){
                    if(entry.getKey().isCrashedPlayer(player) ){
                        try {
                            entry.getValue().handleRejoinedPlayer(player);
                        } catch (PlayerNotFoundException ignored) {
                        }
                        return Optional.of(entry.getValue());
                    }
                }
                throw e;
            }
        }

    }

    /**
     * add a logged player to a game
     * @param player the nickname of the player to be added
     * @return an optional of GameController, if no game is in the lobby, an empty value will be filled
     */
    public RemoteGameController addPlayerToGame(String player) throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException, InvalidPlayerException, PlayersNumberOutOfRange {
        synchronized (lobbyLock) {

            GameController gameController;

            GameModelInterface gameModel = lobbyModel.addPlayerToGame(player);
            gameController = gameControllers.get(gameModel);

            try {
                if (gameController.getGameControlled().canStart())
                    gameController.getGameControlled().start();
            } catch (NotAllPlayersHaveJoinedException | GameNotEndedException ignored) {

            }

            this.stopTimer(player);
            return gameController;
        }
    }


    /**
     * creates a game for a certain number of players
     *
     * @param player   the username of the player
     * @param nPlayers the number of players for the game
     * @return the GameController, if the game creation is not possible, an empty value will be returned
     */
    public RemoteGameController createGame(String player, int nPlayers) throws RemoteException, InvalidPlayerException, BrokenInternalGameConfigurations, PlayersNumberOutOfRange {
        synchronized (lobbyLock) {
            GameController gameController;
            GameModelInterface gameModel = lobbyModel.createGame(nPlayers, player);
            gameController = new GameController(gameModel);
            this.gameControllers.put(gameModel, gameController);
            if(timers.containsKey(player))
                this.stopTimer(player);
            return gameController;
        }
    }

    /**
     * used to handle the crash of the player, it removes the player from the waiting list
     * @param username the username of the player
     * @throws PlayerNotFoundException if the player doesn't exist in the lobby
     * @throws RemoteException
     */
    public void handleCrashedPlayer(String username) throws PlayerNotFoundException, RemoteException {
        synchronized (lobbyLock) {
            this.lobbyModel.handleCrashedPlayer(username);
        }
    }

    /**
     * private method used for initialize the timer of the player
     * @param username the username of the player
     */
    private void initializeTimer(String username){
        this.timers.put(username, new ReschedulableTimer());
        this.timers.get(username).schedule(() -> {
            try {
                this.handleCrashedPlayer(username);
            } catch (PlayerNotFoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        },this.timerDelay);
    }

    /**
     * stops the timer of the lobby for the player with the given username
     * @param username the username of the player
     */
    private void stopTimer(String username){
        this.timers.get(username).cancel();
    }

    /**
     * method used in RMI for triggering the heartbeat of the client,
     * if a client doesn't trigger this method for a too long period, the client will be considered crashed
     * @param username the username of the player
     * @throws RemoteException
     */
    public void triggerHeartBeat(String username) throws RemoteException{
        synchronized (this.timers){
            if(this.timers.get(username) == null ){
                this.initializeTimer(username);
                return;
            }
            this.timers.get(username).reschedule(this.timerDelay);
        }
    }

}
