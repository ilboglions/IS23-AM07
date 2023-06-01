package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.GameStateSubscriber;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.game.GameModelInterface;

import java.rmi.RemoteException;
import java.util.Set;

public class GameStateListener extends Listener<GameStateSubscriber> {

    public void notifyChangedGameState(GameState newState, GameModelInterface gameModel) throws RemoteException {
        Set<GameStateSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyChangedGameStatus(newState, gameModel);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
