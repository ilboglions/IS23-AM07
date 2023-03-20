package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * the bookshelf class stores the tiles taken by the players in the game, it can also been use for a bookshelf pattern reference in cards
 */
public abstract class Bookshelf {
    /**
     * the matrix that stores the tiles assigned
     */
    protected ItemTile[][] tiles;
    /**
     * the number of rows of the bookshelf
     */
    private final int rows;
    /**
     * the number of columns of the bookshelf
     */
    private final int columns;

    /**
     * the constructor initialize the bookshelf
     */
    public Bookshelf() {
        this.tiles = new ItemTile[6][5];
        this.rows = 6;
        this.columns = 5;
    }

    /**
     *
     * @return the number of rows of the bookshelf
     */
    public int getRows(){
        return this.rows;
    }

    /**
     *
     * @return the number of columns of the bookshelf
     */
    public int getColumns(){
        return this.columns;
    }

    /**
     * the method return all the tiles in the bookshelf
     * @return a Map which represent the Item tile and the number of occurrence of that item
     */
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

    /**
     * it returns, if is present, the itemTile in a specific coordinate
     * @param c the coordinate to check
     * @return an optional of the item tile, which contains the tile, if is present
     * @throws IndexOutOfBoundsException if the index required is outside the matrix domain
     */
    public Optional<ItemTile> getItemTile(Coordinates c) throws IndexOutOfBoundsException{
        if(c.getX() >= columns || c.getY() >= rows) {
            throw new IndexOutOfBoundsException("Given coordinates are out of range");
        }

        return Optional.ofNullable(tiles[c.getX()][c.getY()]);
    }
}
