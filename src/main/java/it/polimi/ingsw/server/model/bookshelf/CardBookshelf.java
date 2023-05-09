package it.polimi.ingsw.server.model.bookshelf;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Map;
import java.util.Objects;

/**
 * cardBookshelf is an immutable extension of the bookshelf class, that make it possible to create a pattern used in personal cards
 */
public class CardBookshelf extends Bookshelf{
    /**
     * the constructor creates a bookshelf with a certain pattern
     * @param pattern the pattern used to create the bookshelf
     */
    public CardBookshelf(Map<Coordinates, ItemTile> pattern) {
        Objects.requireNonNull(pattern, "You passed a null instead of a Map object for the pattern");

        for(Map.Entry<Coordinates, ItemTile> tileEntry : pattern.entrySet()) {
            this.tiles[tileEntry.getKey().getRow()][tileEntry.getKey().getColumn()] = tileEntry.getValue();
        }
    }
}
