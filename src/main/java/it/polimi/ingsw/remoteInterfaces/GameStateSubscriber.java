package it.polimi.ingsw.remoteInterfaces;


import it.polimi.ingsw.GameState;
import it.polimi.ingsw.server.model.game.GameModelInterface;

import java.rmi.RemoteException;

/**
 * the interface that provides the method to be implemented by an observer of game state
 */
public interface GameStateSubscriber extends ListenerSubscriber {

    /**
     * when there's an update about the state of the game, the listener will trigger this method
     * @param newState new state of the game
     * @param gameModelInterface reference to the game model
     * @throws RemoteException RMI Exception
     */
    void notifyChangedGameStatus(GameState newState, GameModelInterface gameModelInterface) throws RemoteException;
}
