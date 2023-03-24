package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.tiles.ItemTile;

public class NsameTiles extends  CommonGoalCard{
    private final int nTiles;
    public NsameTiles(int nPlayers, String description , int nTiles) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nTiles <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nTiles = nTiles;
    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        for (ItemTile item : ItemTile.values()){
            if (bookshelf.getAllItemTiles().get(item) >= this.nTiles)
                return true;
        }

        return false;
    }

}
