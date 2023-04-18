package it.polimi.ingsw.server.model.observers;

/**
 * the interface that provides the method to be implemented by an observer of the Player
 */
public interface PlayerObserver extends ListenerObserver {

    /**
     * provides information about the points of a certain player
     * @param player the player that have updated the point
     * @param overallPoints the overall points of the player
     * @param addedPoints the points added on this state change
     */
    void updatePoints(String player, int overallPoints, int addedPoints);
}
