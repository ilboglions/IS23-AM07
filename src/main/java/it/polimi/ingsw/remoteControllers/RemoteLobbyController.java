package it.polimi.ingsw.remoteControllers;

import java.rmi.*;
import java.util.Optional;

public interface RemoteLobbyController extends Remote{
    boolean enterInLobby(String player);
    Optional<RemoteGameController> addPlayerToGame(String player);

    Optional<RemoteGameController> createGame(String player, int nPlayers);
}
