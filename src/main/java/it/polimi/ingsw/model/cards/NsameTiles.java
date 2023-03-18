package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;

public class NsameTiles extends  CommonGoalCard{
    private final int nTiles;
    public NsameTiles(int nPlayers, String description , int nTiles) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers, description);
        if( nTiles <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nTiles = nTiles;
    }
}
