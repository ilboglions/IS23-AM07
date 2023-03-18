package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.bookshelf.exceptions.NotEmptyException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

public class PlayerBookshelf extends Bookshelf{

    public void insertItemTile(Coordinates c, ItemTile tile) throws NotEmptyException {
        if (this.tiles[c.getX()][c.getY()] == null)
            this.tiles[c.getX()][c.getY()] = tile;
        else
            throw new NotEmptyException("This tile is already assigned!");
    }
}
