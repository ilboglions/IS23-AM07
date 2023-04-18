package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.lobby.Lobby;
import it.polimi.ingsw.remoteControllers.RemoteGameController;
import it.polimi.ingsw.remoteControllers.RemoteLobbyController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.rmi.*;
import java.rmi.server.*;
/**
 * the lobby controller ensures the communication through client controller and server model
 */
public class LobbyController extends UnicastRemoteObject implements RemoteLobbyController {
    private final Lobby lobbyModel;
    private final Map<GameModelInterface,GameController> gameControllers;

    /**
     * creates the controller of the lobby
     * @param lobbyModel the model of the lobby
     */
    public LobbyController(Lobby lobbyModel) throws RemoteException {
        super();
        this.lobbyModel = lobbyModel;
        gameControllers = new HashMap<>();
    }

    /**
     * make it possible for the player to join the lobby e select a game
     * @param player the username of the player to be added
     * @return true, if a player can join the lobby, false if the nickname in the lobby has been already used
     */
    public boolean enterInLobby(String player) throws RemoteException{
        try {
            lobbyModel.createPlayer(player);
            return true;
        } catch (NicknameAlreadyUsedException e) {
            return false;
        }
    }

    /**
     * ensures for a player to be added to an available game
     *
     * @param player the nickname of the player to be added
     * @return an optional of GameController, if no game is in the lobby, an empty value will be filled
     */
    public Optional<RemoteGameController> addPlayerToGame(String player) throws RemoteException{
        GameController  gameController;
        try {
            GameModelInterface gameModel = lobbyModel.addPlayerToGame(player);

                gameController = gameControllers.get(gameModel);


        } catch (NoAvailableGameException e) {
            return Optional.empty();
        } catch (InvalidPlayerException e) {
            return Optional.empty();
        } catch (NicknameAlreadyUsedException e) {
            return Optional.empty();
        } catch (PlayersNumberOutOfRange e) {
            return Optional.empty();
        } catch (NullPointerException e) {
            return Optional.empty();
        }

        try {
            if( gameController.getGameControlled().canStart())
                gameController.getGameControlled().start();
        } catch (NotAllPlayersHaveJoinedException ignored) {

        } catch (GameNotEndedException e) {
            return Optional.empty();
        }

        return Optional.of(gameController);
    }

    /**
     * creates a game for a certain number of players
     *
     * @param player   the username of the player
     * @param nPlayers the number of players for the game
     * @return the GameController, if the game creation is not possible, an empty value will be returned
     */
    public Optional<RemoteGameController> createGame(String player, int nPlayers) throws RemoteException{
            GameController  gameController;
            try {
                GameModelInterface gameModel = lobbyModel.createGame(nPlayers,player);
                gameController = new GameController(gameModel);
                this.gameControllers.put(gameModel,gameController);
            } catch (BrokenInternalGameConfigurations e) {
                return Optional.empty();
            } catch (InvalidPlayerException e) {
                return Optional.empty();
            } catch (NotEnoughCardsException e) {
                return Optional.empty();
            } catch (PlayersNumberOutOfRange e) {
                return Optional.empty();
            } catch (NullPointerException e) {
                return Optional.empty();
            }
        return Optional.of(gameController);
    }

}
