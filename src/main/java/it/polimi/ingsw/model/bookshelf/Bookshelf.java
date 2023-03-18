package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

public abstract class Bookshelf {
    protected ItemTile[][] tiles;
    private final int rows;
    private final int columns;

    public Bookshelf() {
        this.tiles = new ItemTile[6][5];
        this.rows = 6;
        this.columns = 5;
    }

    public int getRows(){
        return this.rows;
    }

    public int getColumns(){
        return this.columns;
    }

    public ItemTile getItemType(Coordinates c) {
        return tiles[c.getX()][c.getY()];
    }
}
