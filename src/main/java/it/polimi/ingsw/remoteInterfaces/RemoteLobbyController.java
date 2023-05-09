package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.*;

public interface RemoteLobbyController extends Remote{
    RemoteGameController enterInLobby(String player) throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException;
    void triggerHeartBeat(String username) throws RemoteException;
    RemoteGameController addPlayerToGame(String player) throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException, InvalidPlayerException;

    RemoteGameController createGame(String player, int nPlayers) throws RemoteException, InvalidPlayerException, PlayersNumberOutOfRange;
}
