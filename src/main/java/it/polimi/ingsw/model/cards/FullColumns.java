package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;

public class FullColumns extends CommonGoalCard{
    private final int nCols;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullColumns(int nPlayers, String description , int nCols, boolean sameTiles, int maxTilesFrule) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers,description);
        if( nCols <= 0 || maxTilesFrule <= 0 ) throw new NegativeFieldException("can't assign negative parameters!");
        this.nCols = nCols;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;
    }
}
