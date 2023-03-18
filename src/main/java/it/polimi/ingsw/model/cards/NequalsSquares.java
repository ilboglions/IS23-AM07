package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;

public class NequalsSquares extends CommonGoalCard{

    private final int nSquares;
    private final int squareDim;
    public NequalsSquares(int nPlayers, String description , int nSquares, int squareDim) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers, description);
        if(nSquares <= 0 || squareDim <= 0) throw new NegativeFieldException("can't assign negative parameters!");
        this.nSquares = nSquares;
        this.squareDim = squareDim;
    }
}
