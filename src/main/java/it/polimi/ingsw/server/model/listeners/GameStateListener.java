package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.GameStateSubscriber;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;

import java.rmi.RemoteException;
import java.util.Set;

public class GameStateListener extends Listener<GameStateSubscriber> {

    public void onGameStateChange(GameState newState) {
        Set<GameStateSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyChangeGameStatus(newState);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
