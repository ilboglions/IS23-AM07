package it.polimi.ingsw.server.model.listeners;

import java.util.ArrayList;
import java.util.Set;

import it.polimi.ingsw.remoteControllers.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.subscriber.PlayerSubscriber;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

/**
 * listener of the Player class, it provides to observers information about the player change of state
 */
public class PlayerListener extends Listener<PlayerSubscriber> {

    /**
     * provides information of the player point updates
     * @param player the username of the player
     * @param overallPoints the overall points of a player
     * @param addedPoints the added points of the player
     */
    public void onPointsUpdate(String player, int overallPoints, int addedPoints) {

        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            sub.updatePoints(player, overallPoints, addedPoints);
        }
    }

    public void onTokenPointAcquired(String player, ArrayList<ScoringToken> tokens){
        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            sub.updateTokens(player, tokens);
        }
    }

    public void onPersonalGoalCardAssigned(String player, RemotePersonalGoalCard personalGoalCard, String username){
        Set<PlayerSubscriber> subscribers = this.getSubscribers();

        for (PlayerSubscriber sub : subscribers) {
            if( sub.getSubscriberUsername().equals(username)){
                sub.updatePersonalGoalCard(player, personalGoalCard);
            }
        }
    }
}
