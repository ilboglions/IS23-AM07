package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.Map;

public class CardBookshelf extends Bookshelf{
    public CardBookshelf(Map<Coordinates, ItemTile> pattern) {
        for(Map.Entry<Coordinates, ItemTile> tileEntry : pattern.entrySet()) {
            this.tiles[tileEntry.getKey().getY()][tileEntry.getKey().getX()] = tileEntry.getValue();
        }
    }
}
