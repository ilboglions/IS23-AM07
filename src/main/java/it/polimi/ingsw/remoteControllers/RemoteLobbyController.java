package it.polimi.ingsw.remoteControllers;

import java.rmi.*;
import java.util.Optional;

public interface RemoteLobbyController extends Remote{
    boolean enterInLobby(String player);
    String addPlayerToGame(String player);

    String createGame(String player, int nPlayers);
}
