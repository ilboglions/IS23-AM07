package it.polimi.ingsw.remoteInterfaces;


import it.polimi.ingsw.GameState;

import java.rmi.RemoteException;

public interface GameStateSubscriber extends ListenerSubscriber {

    void notifyChangeGameStatus(GameState newState) throws RemoteException;
}
