package it.polimi.ingsw.remoteInterfaces;


import it.polimi.ingsw.GameState;
import it.polimi.ingsw.server.model.game.GameModelInterface;

import java.rmi.RemoteException;

public interface GameStateSubscriber extends ListenerSubscriber {

    void notifyChangedGameStatus(GameState newState, GameModelInterface gameModelInterface) throws RemoteException;
}
