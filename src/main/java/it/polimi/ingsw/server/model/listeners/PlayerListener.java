package it.polimi.ingsw.server.model.listeners;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.remoteInterfaces.PlayerSubscriber;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

/**
 * listener of the Player class, it provides to observers information about the player change of state
 */
public class PlayerListener extends Listener<PlayerSubscriber> {

    /**
     * Provides information of the player point updates
     * @param player the username of the player
     * @param overallPoints the overall points of a player
     * @param addedPoints the added points of the player
     */
    public void onPointsUpdate(String player, int overallPoints, int addedPoints) throws RemoteException {

        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            sub.updatePoints(player, overallPoints, addedPoints);
        }
    }

    /**
     * Provides information about the token acquired by the player
     * @param player the username of the player
     * @param tokens the current tokens of the player
     */
    public void onTokenPointAcquired(String player, ArrayList<ScoringToken> tokens) throws RemoteException {
        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            sub.updateTokens(player, tokens);
        }
    }

    /**
     * This method is used to give the corresponding player information about his personal goal
     * @param player the username of the player
     * @param personalGoalCard the RemotePersonalGoalCard that was assigned to the player
     */
    public void onPersonalGoalCardAssigned(String player, RemotePersonalGoalCard personalGoalCard) throws RemoteException {
        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            if( sub.getSubscriberUsername().equals(player)){
                sub.updatePersonalGoalCard(player, personalGoalCard);
            }
        }
    }

    /**
     * Method used to trigger the listener when a player joins or re-joins a game after a crash, to receive the complete status of the player
     * @param username the username of the player
     * @param userToBeUpdated the username of the user that needs to receive the updates
     * @param points the current points of the player
     * @param tokens the current tokens of the player
     */
    public void triggerListener(String username, String userToBeUpdated, int points, ArrayList<ScoringToken> tokens) throws RemoteException {
        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            if(sub.getSubscriberUsername().equals(userToBeUpdated)){
                sub.updatePoints(username, points, 0);
                sub.updateTokens(username, tokens);
            }
        }
    }
}
