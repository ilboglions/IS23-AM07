package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.*;

public class NequalsSquares extends CommonGoalCard{

    private final int nSquares;
    private final int squareDim;
    public NequalsSquares(int nPlayers, String description, CommonCardType name, int nSquares, int squareDim) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description, name);
        if(nSquares <= 0 || squareDim <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nSquares = nSquares;
        this.squareDim = squareDim;
    }


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
                    throw new RuntimeException(e);
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
                    throw new RuntimeException(e);
                }

                Optional<ItemTile> brokenItem = parentTiles.stream().filter(tile -> !tile.equals(refTile)).findAny();
                if (brokenItem.isEmpty() && notCloseSameTiles)
                    nFoundSquares++;

            }
        }


        return nFoundSquares >= nSquares;
    }
}
