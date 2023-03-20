package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.Map;

/**
 * cardBookshelf is an immutable extension of the bookshelf class, that make it possible to create a pattern used in personal cards
 */
public class CardBookshelf extends Bookshelf{
    /**
     * the constructor creates a bookshelf with a certain pattern
     * @param pattern the pattern used to create the bookshelf
     */
    public CardBookshelf(Map<Coordinates, ItemTile> pattern) {
        for(Map.Entry<Coordinates, ItemTile> tileEntry : pattern.entrySet()) {
            this.tiles[tileEntry.getKey().getY()][tileEntry.getKey().getX()] = tileEntry.getValue();
        }
    }
}
