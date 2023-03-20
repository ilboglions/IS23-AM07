package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.distributable.exception.NumElementsNegativeException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class BagHolder implements Distributable {

    private ArrayList<ItemTile> tileList;
    private int numTiles; //how many tiles there are in the bag

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
    }

    @Override
    public ArrayList<ItemTile> draw(int nElements) throws NumElementsNegativeException {
        ArrayList<ItemTile> selected;
        if(nElements < 0) {
            throw new NumElementsNegativeException("Num elements to be drawn is negative");
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

    public void shuffle() {
        Collections.shuffle(tileList);
    }
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
