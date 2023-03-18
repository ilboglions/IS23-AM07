package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import java.util.ArrayList;

public class CheckPattern extends CommonGoalCard{
    private final ArrayList<Coordinates> pattern;
    private final boolean sameTiles;
    public CheckPattern(int nPlayers, String description, ArrayList<Coordinates> pattern, boolean sameTiles) throws tooManyPlayersException {

        super(nPlayers, description);

        this.pattern = new ArrayList<Coordinates>();
        this.pattern.addAll(pattern);
        this.sameTiles = sameTiles;


    }
}
