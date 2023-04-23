package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
/**
 * provides updates of the board state
 */
public class BoardListener extends Listener<BoardSubscriber> {

    /**
     * when the tiles on the boards change, the observers are notified
     * @param tilesInBoard the tiles in the board, with a map containing Coordinates and the item tile in that coordinate
     */
    public void onBoardChange(Map<Coordinates, Optional<ItemTile>> tilesInBoard ){

        Set<BoardSubscriber> observers = this.getSubscribers();
        for (BoardSubscriber ob : observers) {
            ob.UpdateBoardStatus(tilesInBoard);
        }
    }

}
