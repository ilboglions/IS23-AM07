package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.rmi.RemoteException;
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
     * @throws RemoteException RMI Exception
     */
    void updatePoints(String player, int overallPoints, int addedPoints) throws RemoteException;

    /**
     * This method is called when there's an update in the ScoringToken list received by user
     * It updates the client
     * @param player the username of the player
     * @param tokenPoints the updated List of  ScoringToken received by the player
     * @throws RemoteException RMI Exception
     */
    void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) throws RemoteException;

    /**
     * This method is called to update the player about their personalGoalCard
     * It updates the client
     * @param player username of the player
     * @param remotePersonal new PersonalGoalCard
     * @throws RemoteException RMI Exception
     */
    void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) throws RemoteException;
}
