package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.tiles.ItemTile;

public class NsameTiles extends  CommonGoalCard{
    private final int nTiles;
    public NsameTiles(int nPlayers, String description , int nTiles) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nTiles <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nTiles = nTiles;
    }

    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        if(nTiles > bookshelf.getRows()*bookshelf.getColumns()) throw new NotEnoughSpaceException(nTiles+" required, but only "+bookshelf.getRows()*bookshelf.getColumns()+" available!");
        for (ItemTile item : ItemTile.values()){
            if (bookshelf.getAllItemTiles().get(item) >= this.nTiles)
                return true;
        }

        return false;
    }

}
