package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.*;

public interface RemoteLobbyController extends Remote{
    boolean enterInLobby(String player) throws RemoteException;
    void triggerHeartBeat(String username) throws RemoteException;
    RemoteGameController addPlayerToGame(String player) throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException, InvalidPlayerException, PlayersNumberOutOfRange;

    RemoteGameController createGame(String player, int nPlayers) throws RemoteException, InvalidPlayerException, BrokenInternalGameConfigurations, NotEnoughCardsException, PlayersNumberOutOfRange;
}
