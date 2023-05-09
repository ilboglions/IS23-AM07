package it.polimi.ingsw.remoteInterfaces;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface GameSubscriber extends ListenerSubscriber {
    /**
     * This is used to notify when a new player has joined the game
     * @param username the username of the player that has joined
     * @throws RemoteException
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
     * @throws RemoteException
     */
    void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards);

    void notifyPlayerInTurn(String username);
}
