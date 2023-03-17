package it.polimi.ingsw.model.cards;

public class FullRows extends CommonGoalCard{
    private final int nRows;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullRows(int nPlayers, int nRows, boolean sameTiles, int maxTilesFrule) throws tooManyPlayersException {
        super(nPlayers);
        this.nRows = nRows;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;
    }
}
