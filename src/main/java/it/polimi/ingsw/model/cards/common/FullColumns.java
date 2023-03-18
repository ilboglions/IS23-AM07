package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

public class FullColumns extends CommonGoalCard{
    private final int nCols;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullColumns(int nPlayers, String description , int nCols, boolean sameTiles, int maxTilesFrule) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers,description);
        if( nCols <= 0 || maxTilesFrule <= 0 ) throw new NegativeFieldException("can't assign negative parameters!");
        this.nCols = nCols;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;
    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        int foundColumns;
        ArrayList<ItemTile> parentTiles= new ArrayList<>();

        foundColumns = 0;

        //how many equals/different tiles? equals -> how many equals?
        int distinctElements = this.sameTiles ? (bookshelf.getRows()  - this.maxTilesFrule)  : bookshelf.getRows();

        for( int c  = 0; c < bookshelf.getColumns(); c++){

            int r  = 0;
            while(r < bookshelf.getRows() && bookshelf.getItemTile(new Coordinates(r,c)).isPresent()){

                parentTiles.add(bookshelf.getItemTile(new Coordinates(r,c)).get());

                r++;
            }

            foundColumns =  (  parentTiles.size() == bookshelf.getRows() && parentTiles.stream().distinct().count() >= distinctElements) ? foundColumns + 1 : foundColumns;

            if ( foundColumns == this.nCols) return true;

            parentTiles.clear();
        }

        return false;
    }
}
