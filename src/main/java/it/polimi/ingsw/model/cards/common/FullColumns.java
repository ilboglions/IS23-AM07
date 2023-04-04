package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

public class FullColumns extends CommonGoalCard{
    /**
     * the number of columns to be checked
     */
    private final int nCols;
    /**
     * used to specify if the columns should contain equals tiles
     */
    private final boolean sameTiles;
    /**
     * the maximum/minimum of possible different tiles in a single column
     * if sameTiles == true --> maximum number
     * if sameTiles == false --> minimum number
     */
    private final int numDifferent;
    public FullColumns(int nPlayers, String description , int nCols, boolean sameTiles, int numDifferent) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers,description);
        if( nCols <= 0 || numDifferent <= 0 ) throw new NegativeFieldException("can't assign negative parameters!");
        this.nCols = nCols;
        this.sameTiles = sameTiles;
        this.numDifferent = numDifferent;
    }

    /**
     * verify that exists at least nCols columns that follow the constraint. If sameTiles is true, the rows should have a number of same tiles specified in the field maxTilesFRule
     * @param bookshelf the bookshelf to be checked
     * @return true, if the constraint is followed
     * @throws NotEnoughSpaceException if the bookshelf doesn't have enough space for the constraint verification
     */
    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        int foundColumns;
        ArrayList<ItemTile> parentTiles= new ArrayList<>();


        if(nCols >= bookshelf.getColumns()) throw new NotEnoughSpaceException("can't check "+nCols+" rows, only "+bookshelf.getColumns()+" available!");


        foundColumns = 0;

        try{
            for( int c  = 0; c < bookshelf.getColumns(); c++){

                int r  = 0;
                while(r < bookshelf.getRows() && bookshelf.getItemTile(new Coordinates(r,c)).isPresent()){

                    parentTiles.add(bookshelf.getItemTile(new Coordinates(r,c)).get());

                    r++;
                }

                if(sameTiles)
                    foundColumns = (parentTiles.size() == bookshelf.getRows() && parentTiles.stream().distinct().count() <= numDifferent) ? foundColumns  + 1 : foundColumns ;
                else
                    foundColumns= (parentTiles.size() == bookshelf.getRows() && parentTiles.stream().distinct().count() >= numDifferent) ? foundColumns + 1 : foundColumns ;


                if ( foundColumns == this.nCols) return true;

                parentTiles.clear();
            }
        } catch (InvalidCoordinatesException ignored){

        }


        return false;
    }
}
