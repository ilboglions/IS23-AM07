package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.GameStateSubscriber;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.game.GameModelInterface;

import java.rmi.RemoteException;
import java.util.Set;

/**
 * the GameStateListener is used to notify the client about updates concerning the state of the game
 */
public class GameStateListener extends Listener<GameStateSubscriber> {

    /**
     * Notifies about the state of the game
     * @param newState new state of the game
     * @param gameModel reference to the game model
     */
    public void notifyChangedGameState(GameState newState, GameModelInterface gameModel) {
        Set<GameStateSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyChangedGameStatus(newState, gameModel);
            } catch (RemoteException ignored) {}
        });
    }

}
