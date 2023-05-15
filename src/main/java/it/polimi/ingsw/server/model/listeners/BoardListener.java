package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
/**
 * provides updates of the board state
 */
public class BoardListener extends Listener<BoardSubscriber> {

    /**
     * when the tiles on the boards change, the observers are notified
     * @param tilesInBoard the tiles in the board, with a map containing Coordinates and the item tile in that coordinate
     */
    public void onBoardChange(Map<Coordinates, ItemTile> tilesInBoard ){

        Set<BoardSubscriber> observers = this.getSubscribers();
        for (BoardSubscriber ob : observers) {
            try {
                ob.updateBoardStatus(tilesInBoard);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method used to trigger listener when a player joins or re-joins a game after a crash, to receive the complete status of the board
     * @param userToBeUpdated the username of the user that needs to receive the updates
     * @param currentTilesMap is the map of each coordinate of the board with the corresponding ItemTile or Optional if empty
     */
    public void triggerListener(String userToBeUpdated, Map<Coordinates, ItemTile> currentTilesMap){
        Set<BoardSubscriber> subscribers = this.getSubscribers();

        for (BoardSubscriber sub : subscribers) {
            try {
                if(sub.getSubscriberUsername().equals(userToBeUpdated)){
                    sub.updateBoardStatus(currentTilesMap);
                }
            } catch (RemoteException ignored) {
            }
        }
    }
}
