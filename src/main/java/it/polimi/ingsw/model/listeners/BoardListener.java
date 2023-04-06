package it.polimi.ingsw.model.listeners;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.observers.BoardObserver;
import it.polimi.ingsw.model.tiles.ItemTile;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
/**
 * provides updates of the board state
 */
public class BoardListener extends Listener<BoardObserver> {

    /**
     * when the tiles on the boards change, the observers are notified
     * @param tilesInBoard the tiles in the board, with a map containing Coordinates and the item tile in that coordinate
     */
    public void onBoardChange(Map<Coordinates, Optional<ItemTile>> tilesInBoard ){

        Set<BoardObserver> observers = this.getObservers();
        for (BoardObserver ob : observers) {
            ob.UpdateBoardStatus(tilesInBoard);
        }
    }

}
