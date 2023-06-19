package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.GameState;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface GameSubscriber extends ListenerSubscriber {
    /**
     * This is used to notify when a new player has joined the game
     * @param username the username of the player that has joined
     * @throws RemoteException RMI Exception
     */
    void notifyPlayerJoined(String username) throws RemoteException;

    /**
     * This is used to notify when a player has won the game
     * @param username the username of the winning player
     * @param points the points of the player that have won the game
     * @param scoreboard the total scoreboard, already ordered, the key is the username and the value the points of the user
     * @throws RemoteException
     */
    void notifyWinningPlayer(String username, int points, Map<String,Integer> scoreboard) throws RemoteException;

    /**
     * This is used to notify to a new player the common goals of the game
     * @param commonGoalCards is the list of the common goals of the game
     * @throws RemoteException RMI Exception
     */
    void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException;

    /**
     * This method is called to notify that it's change in the turn
     * It updates the client
     * @param username username of the player now in turn
     * @throws RemoteException RMI Exception
     */
    void notifyPlayerInTurn(String username) throws RemoteException;

    /**
     * This method is called to notify that a player crashed
     * It updates the client
     * @param userCrashed username of the crashed player
     * @throws RemoteException RMI Exception
     */
    void notifyPlayerCrashed(String userCrashed) throws RemoteException;

    /**
     * This method is called to notify the turn order of the players
     * It updates the client
     * @param playerOrder list of the players of the game in order of turn
     * @throws RemoteException RMI Exception
     */
    void notifyTurnOrder(ArrayList<String> playerOrder) throws RemoteException;

    /**
     * This method specifies the list of the players that are already in the game when a client joins
     * It updates the client
     * @param alreadyJoinedPlayers set of the players already in the game
     * @throws RemoteException RMI Exception
     */
    void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers) throws RemoteException;

    /**
     * This method is called to notify a change in the state of the game
     * @param newState new state of the game
     * @throws RemoteException RMI Exception
     */
    void notifyChangedGameState(GameState newState) throws RemoteException;
}
