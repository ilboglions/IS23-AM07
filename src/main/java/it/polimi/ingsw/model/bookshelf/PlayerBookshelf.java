package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.bookshelf.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

/**
 * the player bookshelf extends the bookshelf class, it is a mutable class, used for stores players tiles
 */
public class PlayerBookshelf extends Bookshelf{
    /**
     * the method make it possible to insert items in a column
     * @param column the columns where items will be inserted
     * @param orderedTiles the tiles, in order of insertion
     * @throws NotEnoughSpaceException if the column is full, no item will be added
     */
    public void insertItemTile(int column, ArrayList<ItemTile> orderedTiles) throws NotEnoughSpaceException {
        if(checkFreeSpace(column) >= orderedTiles.size()) {
            int firstFreeIndex = 0;

            while(this.tiles[firstFreeIndex][column] != null) {
                firstFreeIndex++;
            }

            for (ItemTile orderedTile : orderedTiles) {
                this.tiles[firstFreeIndex][column] = orderedTile;
                firstFreeIndex++;
            }
        }
        else
            throw new NotEnoughSpaceException("There isn't enough space in this column!");
    }

    /**
     * is a useful method that make it possible to check the free space in a column
     * @param column the column to be checked
     * @return the free space in that column
     */
    private int checkFreeSpace(int column) {
        int count = 0;

        for(int i=0; i<this.getRows(); i++) {
            if(this.tiles[i][column] == null)
                count++;
        }

        return count;
    }
}
