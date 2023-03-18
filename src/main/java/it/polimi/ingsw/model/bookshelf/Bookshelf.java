package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Map<ItemTile, Integer> getAllItemTiles() {
        Map<ItemTile, Integer> bookshelfMap = new HashMap<>();
        for( int i = 0 ; i < this.rows; i++){
            for( int j = 0; j < this.columns; j++){
                this.getItemTile(new Coordinates(i,j))
                        .ifPresent(
                                el -> {
                                    int val = bookshelfMap.get(el) != null ? bookshelfMap.get(el) : 0;
                                    bookshelfMap.put(el, val+1 );
                                });
            }
        }
        return bookshelfMap;
    }
    public Optional<ItemTile> getItemTile(Coordinates c) {
        return Optional.ofNullable(tiles[c.getX()][c.getY()]);
    }
}
