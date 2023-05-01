package it.polimi.ingsw.remoteInterfaces;

import java.util.ArrayList;

public interface GameSubscriber extends ListenerSubscriber {
    /**
     * This is used to notify when a new player has joined the game
     * @param username the username of the player that has joined
     */
    void notifyPlayerJoined(String username);

    /**
     * This is used to notify when a player has won the game
     * @param username the username of the winning player
     */
    void notifyWinningPlayer(String username);

    /**
     * This is used to notify to a new player the common goals of the game
     * @param commonGoalCards is the list of the common goals of the game
     */
    void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards);
}
