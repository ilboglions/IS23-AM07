package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.GameModelInterface;
import it.polimi.ingsw.model.lobby.Lobby;

import java.util.ArrayList;
import java.util.Optional;

/**
 * the lobby controller ensures the communication through client controller and server model
 */
public class LobbyController {
    private final Lobby lobbyModel;
    private final ArrayList<GameController> gameControllers;

    /**
     * creates the controller of the lobby
     * @param lobbyModel the model of the lobby
     */
    public LobbyController(Lobby lobbyModel) {
        this.lobbyModel = lobbyModel;
        gameControllers = new ArrayList<>();
    }

    /**
     * make it possible for the player to join the lobby e select a game
     * @param player the username of the player to be added
     * @return true, if a player can join the lobby, false if the nickname in the lobby has been already used
     */
    public boolean enterInLobby(String player){
        try {
            lobbyModel.createPlayer(player);
            return true;
        } catch (NicknameAlreadyUsedException e) {
            return false;
        }
    }

    /**
     * ensures for a player to be added to an available game
     * @param player the nickname of the player to be added
     * @return an optional of GameController, if no game is in the lobby, an empty value will be filled
     */
    public Optional<GameController> addPlayerToGame(String player) {
        GameController  gameController;
        try {
            GameModelInterface gameModel = lobbyModel.addPlayerToGame(player);
            if (gameControllers.stream().anyMatch(el -> el.getGameControlled().equals(gameModel))){
                gameController = gameControllers.stream().filter(el -> el.getGameControlled().equals(gameModel)).findFirst().get();
            } else {
                gameController = new GameController(gameModel);
                gameControllers.add(gameController);
            }
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
     * @param player the username of the player
     * @param nPlayers the number of players for the game
     * @return the GameController, if the game creation is not possible, an empty value will be returned
     */
    public Optional<GameController> createGame(String player, int nPlayers){
            GameController  gameController;
            try {
                GameModelInterface g = lobbyModel.createGame(nPlayers,player);
                 gameController = new GameController(g);
                 this.gameControllers.add(gameController);

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
