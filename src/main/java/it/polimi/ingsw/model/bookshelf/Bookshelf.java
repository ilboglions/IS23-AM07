package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;

public abstract class Bookshelf {
    protected ItemTile[][] tiles;
    private int rows;
    private int columns;

    public Bookshelf() {
        this.tiles = new ItemTile[6][5];
        this.rows = 6;
        this.columns = 5;
    }

    public ItemTile getItemType(Coordinates c) {
        return tiles[c.getX()][c.getY()];
    }
}
