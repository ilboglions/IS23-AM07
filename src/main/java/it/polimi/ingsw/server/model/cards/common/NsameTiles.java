package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;

/**
 * NSameTiles is a common card type that requires a certain number of equals tiles inside the bookshelf
 */
public class NsameTiles extends  CommonGoalCard{
    private final int nTiles;

    /**
     * Creates a NsameTiles Card
     * @param nPlayers number of players of the game
     * @param description description of the card
     * @param name type of the card
     * @param nTiles number of tiles of the same type
     * @throws PlayersNumberOutOfRange  if the number of players is less than 2 or  greater than 4
     * @throws NegativeFieldException if nTiles is less or equal to 0
     * @throws RemoteException RMI Exception
     */
    public NsameTiles(int nPlayers, String description, CommonCardType name, int nTiles) throws PlayersNumberOutOfRange, NegativeFieldException, RemoteException {
        super(nPlayers, description, name);
        if( nTiles <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nTiles = nTiles;
    }

    /**
     * Verifies if there's nTiles tiles of the same type in the bookshelf
     * @param bookshelf the player bookshelf to verify
     * @return true if the constraint is verified, false otherwise.
     * @throws NotEnoughSpaceException nTiles > size of thebookshelf
     */
    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        if(nTiles > bookshelf.getRows()*bookshelf.getColumns()) throw new NotEnoughSpaceException(nTiles+" required, but only "+bookshelf.getRows()*bookshelf.getColumns()+" available!");
        for (ItemTile item : ItemTile.values()){
            if (bookshelf.getAllItemTiles().get(item) >= this.nTiles)
                return true;
        }

        return false;
    }

}
