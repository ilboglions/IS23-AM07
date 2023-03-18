package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Optional;

public class NequalsSquares extends CommonGoalCard{

    private final int nSquares;
    private final int squareDim;
    public NequalsSquares(int nPlayers, String description , int nSquares, int squareDim) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers, description);
        if(nSquares <= 0 || squareDim <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nSquares = nSquares;
        this.squareDim = squareDim;
    }


    public boolean verifyConstraint(Bookshelf bookshelf){

        ArrayList<ItemTile> parentTiles= new ArrayList<>();
        int foundSquares;

        foundSquares = 0;
        for( int r  = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++){
                if(bookshelf.getItemTile(new Coordinates(r,c)).isEmpty()) continue;

                ItemTile refTile = bookshelf.getItemTile(new Coordinates(r,c)).get();

                for (int i = r; i < this.squareDim; i++){
                    for( int j = c; j < this.squareDim; j++){
                        bookshelf.getItemTile(new Coordinates(i, j)).ifPresent(parentTiles::add);
                    }
                }

                if ( parentTiles.size() != this.squareDim*this.squareDim ) continue;

                Optional<ItemTile> brokenItem = parentTiles.stream().filter(tile -> !tile.equals(refTile)).findAny();
                foundSquares = brokenItem.isEmpty() ? foundSquares + 1 : foundSquares;
                if ( foundSquares == this.nSquares)
                    return true;
                // clear parentTiles array
                parentTiles.clear();


            }
        }

        return false;
    }
}
