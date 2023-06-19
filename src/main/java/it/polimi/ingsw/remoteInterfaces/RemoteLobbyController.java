package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.*;

/**
 * This interface represents the methods exposed by the LobbyController over RMI
 */
public interface RemoteLobbyController extends Remote{
    /**
     * make it possible for the player to join the lobby e select a game
     *
     * @param player the username of the player to be added
     * @return a RemoteGameController, if the player was crashed, null if the player is correctly logged in the  lobby
     * @throws NicknameAlreadyUsedException if the player is already inside a game
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException the username used is invalid
     */
    RemoteGameController enterInLobby(String player) throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException;
    /**
     * method used in RMI for triggering the heartbeat of the client,
     * if a client doesn't trigger this method for a too long period, the client will be considered crashed
     * @param username the username of the player
     * @throws RemoteException RMI Exception
     */
    void triggerHeartBeat(String username) throws RemoteException;
    /**
     * add a logged player to a game
     * @param player the nickname of the player to be added
     * @return remote game controller instance reference
     * @throws RemoteException RMI Exception
     * @throws NicknameAlreadyUsedException the nickname used by the player is already taken
     * @throws NoAvailableGameException no game is available
     * @throws InvalidPlayerException the nickname is invalid
     */
    RemoteGameController addPlayerToGame(String player) throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException, InvalidPlayerException;

    /**
     * creates a game for a certain number of player
     * @param player the username of the player
     * @param nPlayers the number of players for the game
     * @return the GameController, if the game creation is not possible, an empty value will be returned
     * @throws RemoteException RMI Exception
     * @throws InvalidPlayerException the nickname is invalid
     * @throws PlayersNumberOutOfRange the number of player is not valid ( less than 2 or greater than 4 )
     */
    RemoteGameController createGame(String player, int nPlayers) throws RemoteException, InvalidPlayerException, PlayersNumberOutOfRange;

}
