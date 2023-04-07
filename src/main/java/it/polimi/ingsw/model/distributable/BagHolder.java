package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Collections;

/**
 * BagHolder represent the bag used to draw the tiles to put on the LivingRoomBoard
 */
public class BagHolder implements Distributable {
    /**
     * The list of all the tiles that is possible to draw
     */
    private ArrayList<ItemTile> tileList;
    /**
     * The total number of tiles that are inside the bag
     */
    private int numTiles; //how many tiles there are in the bag

    /**
     * Constructor that fill the bag with the same number of each ItemTile type for a total of 132 tiles
     */
    public BagHolder() {
        this.tileList = new ArrayList<ItemTile>();
        this.numTiles = 132;
        for(int i=0; i<22; i++) {
            tileList.add(ItemTile.GAME);
            tileList.add(ItemTile.TROPHY);
            tileList.add(ItemTile.PLANT);
            tileList.add(ItemTile.CAT);
            tileList.add(ItemTile.FRAME);
            tileList.add(ItemTile.BOOK);
        }

        Collections.shuffle(tileList);
    }

    /**
     * This method is used to draw a certain number of tiles from the bag
     * @param nElements the number of elements to draw
     * @return the list of the ItemTiles drawn
     * @throws NegativeFieldException if the number to drawn required is negative
     */
    @Override
    public ArrayList<ItemTile> draw(int nElements) throws NegativeFieldException {
        ArrayList<ItemTile> selected;
        if(nElements < 0) {
            throw new NegativeFieldException("Num elements to be drawn is negative");
        } else {
            selected = new ArrayList<ItemTile>();
            for(int i=0; i<nElements; i++) {
                if(tileList.isEmpty()) {
                    resetBag();
                }
                selected.add(tileList.get(0));
                tileList.remove(0);
                numTiles--; //just for debugging and testing
            }
            return selected;
        }
    }

    /**
     * This method is used to shuffle the tileList
     */
    public void shuffle() {
        Collections.shuffle(tileList);
    }

    /**
     * This method is used to reset the bag to the initial status with the same quantity of each ItemTile
     */
    private void resetBag() {
        ArrayList<ItemTile> temp = new ArrayList<ItemTile>();
        for(int i=0; i<22; i++) {
            temp.add(ItemTile.GAME);
            temp.add(ItemTile.TROPHY);
            temp.add(ItemTile.PLANT);
            temp.add(ItemTile.CAT);
            temp.add(ItemTile.FRAME);
            temp.add(ItemTile.BOOK);
        }
        this.numTiles = 132;
        this.tileList = temp;
        this.shuffle();
    }

}
