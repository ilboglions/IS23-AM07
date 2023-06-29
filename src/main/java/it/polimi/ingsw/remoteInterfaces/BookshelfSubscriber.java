package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
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
     * @param currentTilesMap the map of all the tiles inserted in the bookshelf
     * @throws RemoteException RMI Exception
     */
    void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) throws RemoteException;
}
