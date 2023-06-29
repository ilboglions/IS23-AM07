package it.polimi.ingsw.server.model.bookshelf;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * the bookshelf class stores the tiles taken by the players in the game, it can also been use for a bookshelf pattern reference in cards
 */
public abstract class Bookshelf implements Serializable {
    @Serial
    private static final long serialVersionUID = 7808215898704889350L;
    /**
     * the matrix that stores the tiles assigned
     */
    protected ItemTile[][] tiles;
    /**
     * the number of rows of the bookshelf
     */
    protected final int rows;
    /**
     * the number of columns of the bookshelf
     */
    protected final int columns;

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
                try {
                    this.getItemTile(new Coordinates(i,j))
                            .ifPresent(
                                    el -> {
                                        int val = bookshelfMap.get(el) != null ? bookshelfMap.get(el) : 0;
                                        bookshelfMap.put(el, val+1 );
                                    });
                } catch (InvalidCoordinatesException ignored){} //IGNORED SINCE THIS EXCEPTION IS NEVER THROWN
            }
        }
        for (ItemTile item : ItemTile.values()) {
            bookshelfMap.putIfAbsent(item, 0);
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
        Objects.requireNonNull(c, "You passed a null instead of a Coordinates object");
        if(c.getColumn() >= columns || c.getRow() >= rows) {
            throw new IndexOutOfBoundsException("Given coordinates are out of range");
        }

        return Optional.ofNullable(tiles[c.getRow()][c.getColumn()]);
    }

    /**
     * Method to calculate the number of ItemTiles that are in the same position and equal between this bookshelf and the one passed as parameter
     * @param bookshelf the bookshelf to check the elements on
     * @return an int that is the number of ItemTile in the same position and equal between the two bookshelf
     */
    public int nElementsOverlapped(Bookshelf bookshelf) {
        Objects.requireNonNull(bookshelf, "You passed a null instead of a Bookshelf object");
        int count = 0;
        Coordinates c;

        for(int i=0; i < this.rows; i++) {
            for(int j=0; j < this.columns; j++) {
                try {
                    c = new Coordinates(i,j);
                    if(bookshelf.getItemTile(c).isPresent() && this.getItemTile(c).isPresent() && bookshelf.getItemTile(c).get().equals(this.getItemTile(c).get()))
                        count++;
                } catch (InvalidCoordinatesException ignored) {
                }
            }
        }

        return count;
    }

    /**
     * This method is used to retrieve a complete mapping of the ItemTiles inside the bookshelf
     * @return the map of the coordinates that has a ItemTile in it
     */
    public Map<Coordinates, ItemTile> getItemTileMap(){
        Map<Coordinates, ItemTile> bookshelfMap = new HashMap<>();

        for( int i = 0 ; i < this.rows; i++){
            for( int j = 0; j < this.columns; j++){
                try {
                    Optional<ItemTile> tmp = this.getItemTile(new Coordinates(i,j));
                    if(tmp.isPresent())
                        bookshelfMap.put(new Coordinates(i,j), tmp.get());
                } catch (InvalidCoordinatesException ignored) {

                }
            }
        }

        return bookshelfMap;
    }
}
