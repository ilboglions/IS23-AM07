package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

public class FullRows extends CommonGoalCard{
    private final int nRows;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullRows(int nPlayers,  String description ,int nRows, boolean sameTiles, int maxTilesFrule) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nRows <= 0 || maxTilesFrule <= 0 ) throw new NegativeFieldException("can't assign negative parameters!");
        this.nRows = nRows;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;

    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        int foundRows;
        ArrayList<ItemTile> parentTiles= new ArrayList<>();

        foundRows = 0;

        //how many equals/different tiles? equals -> how many equals?
        int distinctElements = this.sameTiles ? (bookshelf.getRows()  - this.maxTilesFrule)  : bookshelf.getRows();

        for( int r  = 0; r < bookshelf.getRows(); r++){

            int c  = 0;
            while(c < bookshelf.getColumns() && bookshelf.getItemTile(new Coordinates(r,c)).isPresent()){

                parentTiles.add(bookshelf.getItemTile(new Coordinates(r,c)).get());

                c++;
            }

            foundRows =  (  parentTiles.size() == bookshelf.getRows() && parentTiles.stream().distinct().count() >= distinctElements) ? foundRows + 1 : foundRows;

            if ( foundRows == this.nRows) return true;

            parentTiles.clear();
        }

        return false;
    }
}

