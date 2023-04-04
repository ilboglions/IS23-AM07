package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;

/**
 * the fullRows common card check that a bookshelf contains a minimum of nRows full rows which follow a specific rule
 */
public class FullRows extends CommonGoalCard{
    /**
     * the number of rows to be found
     */
    private final int nRows;
    /**
     * if true, the constraint require a minimum of bookshelf.getRows - maxTilesFRule equal tiles, otherwise all the tiles should be different
     */
    private final boolean sameTiles;
    /**
     * the maximum/minimum of possible different tiles in a single row
     * if sameTiles == true --> maximum number
     * if sameTiles == false --> minimum number
     */
    private final int numDifferent;

    /**
     * the constructor of the card
     * @param nPlayers the number of player, used for the stack of points
     * @param description the description of the card
     * @param nRows the number of rows to be found
     * @param sameTiles if true, the constraint require a minimum of bookshelf.getRows - maxTilesFRule equal tiles, otherwise all the tiles should be different
     * @param numDifferent the maximum of possible different tiles in a single row
     * @throws PlayersNumberOutOfRange if the number of player is out of the permitted range
     * @throws NegativeFieldException if some field is negative
     */
    public FullRows(int nPlayers,  String description ,int nRows, boolean sameTiles, int numDifferent) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nRows <= 0 || numDifferent <= 0 ) throw new NegativeFieldException("can't assign negative parameters!");
        this.nRows = nRows;
        this.sameTiles = sameTiles;
        this.numDifferent = numDifferent;

    }

    /**
     * verify, if sameTiles is true, that at least nRows row have rows - maxTileFRule same tiles, otherwise that exists at least nRows rows with all different tiles
     * @param bookshelf the bookshelf to be checked
     * @return true, if the bookshelf follows the constraint
     * @throws NotEnoughSpaceException if the nRows parameter is bigger than the number of rows of the bookshelf
     */
    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        int foundRows;
        ArrayList<ItemTile> parentTiles= new ArrayList<>();

        if(nRows >= bookshelf.getRows()) throw new NotEnoughSpaceException("can't check "+nRows+" rows, only "+bookshelf.getRows()+" available!");
        foundRows = 0;

        try {
            for (int r = 0; r < bookshelf.getRows(); r++) {

                int c = 0;
                while (c < bookshelf.getColumns() && bookshelf.getItemTile(new Coordinates(r, c)).isPresent()) {

                    parentTiles.add(bookshelf.getItemTile(new Coordinates(r, c)).get());

                    c++;
                }

                if(sameTiles)
                    foundRows = (parentTiles.size() == bookshelf.getColumns() && parentTiles.stream().distinct().count() <= numDifferent) ? foundRows + 1 : foundRows;
                else
                    foundRows = (parentTiles.size() == bookshelf.getColumns() && parentTiles.stream().distinct().count() >= numDifferent) ? foundRows + 1 : foundRows;

                if (foundRows == this.nRows) return true;

                parentTiles.clear();
            }
        }catch (InvalidCoordinatesException e){
            throw new RuntimeException(e);
        }

        return false;
    }
}

