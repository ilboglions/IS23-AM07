package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Optional;

/**
 * FiveXtiles is a common card that requires an X made of 5 tiles as the goal.
 */
public class FiveXTiles extends  CommonGoalCard{
    /**
     * if true, the constraint will verify that all the tiles following it are of the same type
     */
    private final boolean sameTiles;
    public FiveXTiles(int nPlayers, String description ,boolean sameTiles) throws PlayersNumberOutOfRange {
        super(nPlayers, description);
        this.sameTiles = sameTiles;
    }

    /**
     * check that exists at least one X formed by five tiles
     * @param bookshelf the bookshelf to be checked
     * @return true, if exists at least one X as scripted
     */
    public boolean verifyConstraint(Bookshelf bookshelf){
        ArrayList<ItemTile> parentTiles= new ArrayList<>();
        int rowlimit = bookshelf.getRows()-2;
        int collimit =bookshelf.getColumns()-2;
        for( int r  = 0; r < rowlimit; r++){
            for( int c = 0; c < collimit; c++) {
                ItemTile refTile;
                try {

                    if (bookshelf.getItemTile(new Coordinates(r, c)).isEmpty()) continue;
                    refTile = bookshelf.getItemTile(new Coordinates(r, c)).get();
                    bookshelf.getItemTile(new Coordinates(r, c + 2)).ifPresent(parentTiles::add);
                    bookshelf.getItemTile(new Coordinates(r + 2, c)).ifPresent(parentTiles::add);
                    bookshelf.getItemTile(new Coordinates(r + 1, c + 1)).ifPresent(parentTiles::add);
                    bookshelf.getItemTile(new Coordinates(r + 2, c + 2)).ifPresent(parentTiles::add);
                } catch (InvalidCoordinatesException e) {
                    throw new RuntimeException(e);
                }

                if (parentTiles.size() != 4) continue;
                if (!this.sameTiles) return true;

                Optional<ItemTile> brokenItem = parentTiles.stream().filter(tile -> !tile.equals(refTile)).findAny();
                if (brokenItem.isEmpty())
                    return true;

                parentTiles.clear();


            }
        }

        return false;
    }
}
