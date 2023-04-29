package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.lobby.Lobby;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.remoteInterfaces.RemoteLobbyController;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import java.rmi.*;
import java.rmi.server.*;

/**
 * the lobby controller ensures the communication through client controller and server model
 */
public class LobbyController extends UnicastRemoteObject implements RemoteLobbyController {
    @Serial
    private static final long serialVersionUID = 172897351161158928L;

    private final Lobby lobbyModel;
    private final Map<GameModelInterface,GameController> gameControllers;
    /*
        lobbyLock is used to synchronize the methods of LobbyController. it is equivalent to use synchronize in the method's signature
     */

    private final Object lobbyLock;
    /**
     * creates the controller of the lobby
     * @param lobbyModel the model of the lobby
     */
    public LobbyController(Lobby lobbyModel) throws RemoteException {
        super();
        this.lobbyModel = lobbyModel;
        gameControllers = new HashMap<>();
        lobbyLock = new Object();
    }

    /**
     * make it possible for the player to join the lobby e select a game
     * @param player the username of the player to be added
     * @return true, if a player can join the lobby, false if the nickname in the lobby has been already used
     */
    public boolean enterInLobby(String player) throws RemoteException{
        synchronized (lobbyLock) {
            try {
                lobbyModel.createPlayer(player);
                return true;
            } catch (NicknameAlreadyUsedException e) {
                return false;
            }
        }

    }

    /**
     * ensures for a player to be added to an available game
     *
     * @param player the nickname of the player to be added
     * @return an optional of GameController, if no game is in the lobby, an empty value will be filled
     */
    public RemoteGameController addPlayerToGame(String player) throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException, InvalidPlayerException, PlayersNumberOutOfRange {
        synchronized (lobbyLock) {

            for( Map.Entry<GameModelInterface, GameController> entry : gameControllers.entrySet()){
                    if(entry.getKey().isCrashedPlayer(player) ){
                        try {
                            entry.getValue().handleRejoinedPlayer(player);
                        } catch (PlayerNotFoundException ignored) {
                        }
                        return entry.getValue();
                    }
            }

            GameController gameController;

            GameModelInterface gameModel = lobbyModel.addPlayerToGame(player);
            gameController = gameControllers.get(gameModel);

            try {
                if (gameController.getGameControlled().canStart())
                    gameController.getGameControlled().start();
            } catch (NotAllPlayersHaveJoinedException | GameNotEndedException ignored) {

            }

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

            return gameController;
        }
    }

}
