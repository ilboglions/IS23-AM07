package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.remoteInterfaces.BookshelfSubscriber;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * provides to the client information about a bookshelf of a player
 */
public class BookshelfListener extends Listener<BookshelfSubscriber> {

    /**
     * when a bookshelf of a player receives new tiles, all the observers are notified
     * @param player the player that has the bookshelf
     * @param insertedTiles the tiles inserted, the first one is the first to be inserted in the bookshelf
     * @param col the column where the tiles are insert
     */
    public void onBookshelfChange(String player, ArrayList<ItemTile> insertedTiles, int col){
        Set<BookshelfSubscriber> observers = this.getSubscribers();

        for (BookshelfSubscriber o : observers) {
            o.updateBookshelfStatus(player, insertedTiles, col);
        }
    }

    /**
     * Method used to trigger the listener when a player joins or re-joins a game after a crash, to receive the complete status of the bookshelf
     * @param userToBeUpdated the username of the user that needs to receive the updates
     * @param currentTilesMap is the map the coordinates of the board with the corresponding ItemTile in it
     */
    public void triggerListener(String userToBeUpdated, Map<Coordinates, ItemTile> currentTilesMap){
        Set<BookshelfSubscriber> subscribers = this.getSubscribers();

        for (BookshelfSubscriber sub : subscribers) {
            if(sub.getSubscriberUsername().equals(userToBeUpdated)){
                sub.updateBookshelfComplete(currentTilesMap);
            }
        }
    }
}
