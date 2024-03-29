package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.*;

/**
 * NequalsSquares is a common card type that requires that the tiles are arranged in a square
 */
public class NequalsSquares extends CommonGoalCard{

    private final int nSquares;
    private final int squareDim;

    /**
     * Creates a NequalsSquares Card
     * @param nPlayers number of players of the game
     * @param description description of the card
     * @param name type of the card
     * @param nSquares number of squares
     * @param squareDim size of the square
     * @throws PlayersNumberOutOfRange if the number of players is less than 2 or greater than 4
     * @throws NegativeFieldException if nSquares is less or equal to 0 or squareDim is less or equal to 0
     * @throws RemoteException RMI Exception
     */
    public NequalsSquares(int nPlayers, String description, CommonCardType name, int nSquares, int squareDim) throws PlayersNumberOutOfRange, NegativeFieldException, RemoteException {
        super(nPlayers, description, name);
        if(nSquares <= 0 || squareDim <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nSquares = nSquares;
        this.squareDim = squareDim;
    }


    /**
     * Verifies if the bookshelf containes nSquares squares of dimensions SquareDim x SquareDim of tiles of the same type
     * @param bookshelf the player bookshelf to verify
     * @return true if the constraint is verified, false otherwise.
     * @throws NotEnoughSpaceException if nSquares * squareDim > size of the bookshelf
     */
    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        ArrayList<ItemTile> parentTiles= new ArrayList<>();
        int nFoundSquares = 0;

        if(nSquares * squareDim * squareDim  > bookshelf.getColumns()*bookshelf.getRows() ) throw  new NotEnoughSpaceException(nSquares+" squares of dimension "+squareDim+" can't be found! not enough tile!" );

        int rowslimit = bookshelf.getRows() - squareDim+1;
        int collimit = bookshelf.getColumns() - squareDim+1;
        for( int r  = 0; r <rowslimit && nFoundSquares < nSquares; r++){
            for( int c = 0; c < collimit && nFoundSquares < nSquares; c++) {

                ItemTile refTile;
                parentTiles.clear();
                try {
                    if (bookshelf.getItemTile(new Coordinates(r, c)).isEmpty()) continue;
                    refTile = bookshelf.getItemTile(new Coordinates(r, c)).get();
                    int squarerowlim = r+squareDim;
                    int suqarecollim = c+squareDim;
                    for (int i = r; i < squarerowlim; i++) {
                        for (int j = c; j < suqarecollim; j++) {
                            Coordinates tempCoords = new Coordinates(i, j);
                                bookshelf.getItemTile(tempCoords).ifPresent(parentTiles::add);
                        }
                    }
                } catch (InvalidCoordinatesException e) {
                    return false;
                }
                if (parentTiles.size() != this.squareDim * this.squareDim) continue;
                boolean notCloseSameTiles = true;
                int highBorder = r + squareDim;
                int lowBorder = r - squareDim;
                int leftBorder = c-squareDim;
                int rightBorder = c+squareDim;
                try {
                    if (highBorder < bookshelf.getRows()) {
                        for (int i = 0; i < squareDim && notCloseSameTiles; i++) {
                            Coordinates tempCoords = new Coordinates(highBorder, i + c);
                            if (bookshelf.getItemTile(tempCoords).isPresent() && bookshelf.getItemTile(tempCoords).get().equals(refTile)){
                                    notCloseSameTiles=false;
                            }
                        }
                    }
                    if (lowBorder >= 0) {
                        for (int i = 0; i < squareDim && notCloseSameTiles; i++) {
                            Coordinates tempCoords = new Coordinates(lowBorder, i + c);
                            if (bookshelf.getItemTile(tempCoords).isPresent() && bookshelf.getItemTile(tempCoords).get().equals(refTile)){
                                notCloseSameTiles=false;
                            }
                        }
                    }
                    if (leftBorder >= 0) {
                        for (int i = 0; i < squareDim && notCloseSameTiles; i++) {
                            Coordinates tempCoords = new Coordinates(i+r, leftBorder);
                            if (bookshelf.getItemTile(tempCoords).isPresent() && bookshelf.getItemTile(tempCoords).get().equals(refTile)){
                                notCloseSameTiles=false;
                            }
                        }
                    }
                    if (rightBorder < bookshelf.getColumns()) {
                        for (int i = 0; i < squareDim && notCloseSameTiles; i++) {
                            Coordinates tempCoords = new Coordinates(i+r, rightBorder);
                            if (bookshelf.getItemTile(tempCoords).isPresent() && bookshelf.getItemTile(tempCoords).get().equals(refTile)){
                                notCloseSameTiles=false;
                            }
                        }
                    }
                }
                catch (InvalidCoordinatesException e) {
                    return false;
                }

                Optional<ItemTile> brokenItem = parentTiles.stream().filter(tile -> !tile.equals(refTile)).findAny();
                if (brokenItem.isEmpty() && notCloseSameTiles)
                    nFoundSquares++;

            }
        }


        return nFoundSquares >= nSquares;
    }
}
