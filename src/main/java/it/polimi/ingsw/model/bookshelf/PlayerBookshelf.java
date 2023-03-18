package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.bookshelf.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

public class PlayerBookshelf extends Bookshelf{

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

    private int checkFreeSpace(int column) {
        int count = 0;

        for(int i=0; i<this.getRows(); i++) {
            if(this.tiles[i][column] == null)
                count++;
        }

        return count;
    }
}
