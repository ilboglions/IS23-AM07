package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.coordinate.Coordinates;
import java.util.ArrayList;

public class checkPattern extends CommonGoalCard{
    private final ArrayList<Coordinates> pattern;
    private final boolean sameTiles;
    public checkPattern(int nPlayers, ArrayList<Coordinates> pattern, boolean sameTiles) throws tooManyPlayersException {

        super(nPlayers);

        this.pattern = new ArrayList<Coordinates>();
        this.pattern.addAll(pattern);
        this.sameTiles = sameTiles;


    }
}
