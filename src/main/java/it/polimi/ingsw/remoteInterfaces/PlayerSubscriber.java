package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.util.ArrayList;

/**
 * the interface that provides the method to be implemented by an observer of the Player
 */
public interface PlayerSubscriber extends ListenerSubscriber {

    /**
     * provides information about the points of a certain player
     * @param player the player that have updated the point
     * @param overallPoints the overall points of the player
     * @param addedPoints the points added on this state change
     */
    void updatePoints(String player, int overallPoints, int addedPoints);

    void updateTokens(String player, ArrayList<ScoringToken> tokenPoints);

    void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal);
}