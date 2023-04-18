package it.polimi.ingsw.server.model.listeners;

import java.util.Set;
import it.polimi.ingsw.server.model.observers.PlayerObserver;

/**
 * listener of the Player class, it provides to observers information about the player change of state
 */
public class PlayerListener extends Listener<PlayerObserver> {

    /**
     * provides information of the player point updates
     * @param player the username of the player
     * @param overallPoints the overall points of a player
     * @param addedPoints the added points of the player
     */
    public void onPointsUpdate(String player, int overallPoints, int addedPoints) {

        Set<PlayerObserver> observers = this.getObservers();

        for (PlayerObserver obs : observers) {
            obs.updatePoints(player, overallPoints, addedPoints);
        }
    }
}
