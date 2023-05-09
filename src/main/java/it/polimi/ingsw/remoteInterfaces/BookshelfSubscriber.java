package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import java.util.ArrayList;
import java.util.Map;

/**
 * the interface that provides the method to be implemented by an observer of the bookshelf
 */
public interface BookshelfSubscriber extends ListenerSubscriber {
    /**
     * the listener will notify all the changes of the bookshelf on this method
     * @param player the username of the players that owns the bookshelf
     * @param tilesInserted the tile inserted by the player
     * @param colChosen the column chosen for the insertion
     */
    void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen );

    /**
     * This method is used to notify the complete current status of the bookshelf
     * @param currentTilesMap the map of the coordinates of the bookshelf with a tile in it
     * @param username the username of the bookshelf owner
     */
    void updateBookshelfComplete(Map<Coordinates, ItemTile> currentTilesMap, String username);
}
