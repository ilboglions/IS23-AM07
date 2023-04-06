package it.polimi.ingsw.model.listeners;

import it.polimi.ingsw.model.observers.BookshelfObserver;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Set;

/**
 * provides to the client information about a bookshelf of a player
 */
public class BookshelfListener extends Listener<BookshelfObserver> {

    /**
     * when a bookshelf of a player receives new tiles, all the observers are notified
     * @param player the player that has the bookshelf
     * @param insertedTiles the tiles inserted, the first one is the first to be inserted in the bookshelf
     * @param col the column where the tiles are insert
     */
    public void onBookshelfChange(String player, ArrayList<ItemTile> insertedTiles, int col){
        Set<BookshelfObserver> observers = this.getObservers();

        for (BookshelfObserver o : observers) {
            o.UpdateBookshelfStatus(player, insertedTiles, col);
        }
    }
}