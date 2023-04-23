package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;

public class NsameTiles extends  CommonGoalCard{
    private final int nTiles;
    public NsameTiles(int nPlayers, String description, CommonCardType name, int nTiles) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description, name);
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
