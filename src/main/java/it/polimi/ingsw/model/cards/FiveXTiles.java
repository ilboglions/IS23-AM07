package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;

public class FiveXTiles extends  CommonGoalCard{
    private final boolean sameTiles;
    public FiveXTiles(int nPlayers, boolean sameTiles) throws tooManyPlayersException {
        super(nPlayers);
        this.sameTiles = sameTiles;
    }
}
