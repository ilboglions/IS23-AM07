package it.polimi.ingsw.remoteControllers;

import java.rmi.*;
import java.util.Optional;

public interface RemoteLobbyController extends Remote{
    boolean enterInLobby(String player) throws RemoteException;
    Optional<RemoteGameController> addPlayerToGame(String player) throws RemoteException;

    Optional<RemoteGameController> createGame(String player, int nPlayers) throws RemoteException;
}
