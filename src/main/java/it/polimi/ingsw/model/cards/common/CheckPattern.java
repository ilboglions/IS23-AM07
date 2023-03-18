package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.util.ArrayList;

public class CheckPattern extends CommonGoalCard{
    private final ArrayList<Coordinates> pattern;
    private final boolean sameTiles;
    public CheckPattern(int nPlayers, String description, ArrayList<Coordinates> pattern, boolean sameTiles) throws tooManyPlayersException {

        super(nPlayers, description);

        this.pattern = new ArrayList<>();
        this.pattern.addAll(pattern);
        this.sameTiles = sameTiles;


    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        if(!sameTiles)
            return  pattern.stream().allMatch(el  -> bookshelf.getItemTile(el).isPresent());

        if (pattern.stream().distinct().count() == 1)
            return pattern.stream().allMatch(el  -> bookshelf.getItemTile(el).isPresent());
        return false;

    }
}
