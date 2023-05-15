package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * the CheckPattern common card is used to verify that a certain pattern is respected in the card.
 */
public class CheckPattern extends CommonGoalCard{
    /**
     * contains all the possible pattern to be found, just one of this pattern should be verified
     */
    private final ArrayList<ArrayList<Coordinates>> pattern;
    /**
     * the sameTiles attribute specify if the tiles in the pattern should be of the same type
     */
    private final boolean sameTiles;

    /**
     * the constructor of the card
     * @param nPlayers the number of player
     * @param description the description of the card
     * @param pattern the possible patterns to verify
     * @param sameTiles true, if the tiles should be the same for all the pattern
     * @throws PlayersNumberOutOfRange if the players number do not respect the specific
     */
    public CheckPattern(int nPlayers, String description, CommonCardType name, ArrayList<ArrayList<Coordinates>> pattern, boolean sameTiles) throws PlayersNumberOutOfRange, RemoteException {

        super(nPlayers, description, name);

        this.pattern = new ArrayList<>();
        this.pattern.addAll(pattern);
        this.sameTiles = sameTiles;


    }

    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException {

        if ( pattern.stream().anyMatch( el -> el.stream().anyMatch( e -> e.getRow() >= bookshelf.getRows() || e.getColumn() >= bookshelf.getColumns()) ) )
            throw new NotEnoughSpaceException("can't check a pattern containing a coordinate out of bookshelf's range!");

        ArrayList<ArrayList<Coordinates>> verifiedPatterns =  pattern.stream().filter(el -> el.stream().allMatch(e  -> bookshelf.getItemTile(e).isPresent())).collect(Collectors.toCollection(ArrayList::new));

        if(!this.sameTiles)
            return verifiedPatterns.size() > 0;


        return verifiedPatterns.stream().anyMatch(
                el -> {

                    if(bookshelf.getItemTile(el.get(0)).isEmpty()) return false;
                    ItemTile refTile = bookshelf.getItemTile(el.get(0)).get();
                    for( Coordinates c: el) {
                        if(bookshelf.getItemTile(c).isEmpty() || !bookshelf.getItemTile(c).get().equals(refTile))
                            return false;
                    }
                    return true;
                });

    }
}
