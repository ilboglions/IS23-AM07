package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;

public class FullRows extends CommonGoalCard{
    private final int nRows;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullRows(int nPlayers, int nRows, boolean sameTiles, int maxTilesFrule) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers);
        if( nRows <= 0 || maxTilesFrule <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nRows = nRows;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;

    }
}
