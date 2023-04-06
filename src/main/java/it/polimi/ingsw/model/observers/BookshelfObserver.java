package it.polimi.ingsw.model.observers;

import it.polimi.ingsw.model.tiles.ItemTile;
import java.util.ArrayList;

/**
 * the interface that provides the method to be implemented by an observer of the bookshelf
 */
public interface BookshelfObserver extends BoardObserver, ListenerObserver {
    /**
     * the listener will notify all the changes of the bookshelf on this method
     * @param player the username of the players that owns the bookshelf
     * @param tilesInserted the tile inserted by the player
     * @param colChosen the column chosen for the insertion
     */
    void UpdateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen );
}
