package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;

public class NadiacentElements extends CommonGoalCard{

    private int nGroups;
    private int nElems;

    public NadiacentElements(int nPlayers, int nGroups, int nElems) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers);
        if( nGroups <= 0 || nElems <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nGroups = nGroups;
        this.nGroups = nElems;
    }
}
