package it.polimi.ingsw.model.cards;

public class FullColumns extends CommonGoalCard{
    private final int nCols;
    private final boolean sameTiles;
    private final int maxTilesFrule;
    public FullColumns(int nPlayers, int nCols, boolean sameTiles, int maxTilesFrule) throws tooManyPlayersException {
        super(nPlayers);
        this.nCols = nCols;
        this.sameTiles = sameTiles;
        this.maxTilesFrule = maxTilesFrule;
    }
}
