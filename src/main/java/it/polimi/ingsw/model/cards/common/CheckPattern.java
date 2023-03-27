package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CheckPattern extends CommonGoalCard{
    private final ArrayList<ArrayList<Coordinates>> pattern;
    private final boolean sameTiles;
    public CheckPattern(int nPlayers, String description, ArrayList<ArrayList<Coordinates>> pattern, boolean sameTiles) throws PlayersNumberOutOfRange {

        super(nPlayers, description);

        this.pattern = new ArrayList<>();
        this.pattern.addAll(pattern);
        this.sameTiles = sameTiles;


    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        ArrayList<ArrayList<Coordinates>> verifiedPatterns =  pattern.stream().filter(el -> el.stream().allMatch(e  -> bookshelf.getItemTile(e).isPresent())).collect(Collectors.toCollection(ArrayList::new));

        if(!this.sameTiles)
            return verifiedPatterns.size() > 0;


        return verifiedPatterns.stream().anyMatch(
                el -> {

                    if(bookshelf.getItemTile(el.get(0)).isEmpty()) return false;
                    ItemTile refTile = bookshelf.getItemTile(el.get(0)).get();
                    boolean stillOk = true;
                    for( Coordinates c: el) {
                        stillOk  = bookshelf.getItemTile(c).isPresent();
                        if(!stillOk)
                            return false;
                        stillOk = bookshelf.getItemTile(c).get().equals(refTile);
                    }

                    return stillOk;
                });

    }
}
