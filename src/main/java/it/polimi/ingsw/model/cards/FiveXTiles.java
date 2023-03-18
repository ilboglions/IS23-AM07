package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.Optional;

public class FiveXTiles extends  CommonGoalCard{
    private final boolean sameTiles;
    public FiveXTiles(int nPlayers, String description ,boolean sameTiles) throws tooManyPlayersException {
        super(nPlayers, description);
        this.sameTiles = sameTiles;
    }

    public boolean verifyConstraint(PlayerBookshelf bookshelf){
        ArrayList<ItemTile> parentTiles= new ArrayList<>();
        for( int r  = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++){
                if(bookshelf.getItemType(new Coordinates(r,c)) != null ){
                            ItemTile refTile = bookshelf.getItemType(new Coordinates(r,c));
                            parentTiles.add(bookshelf.getItemType(new Coordinates(r, c + 2)));
                            parentTiles.add(bookshelf.getItemType(new Coordinates(r+2, c)));
                            parentTiles.add(bookshelf.getItemType(new Coordinates(r+1, c+1)));
                            parentTiles.add(bookshelf.getItemType(new Coordinates(r+2, c+2)));

                            if ( !(parentTiles.contains(null)) )
                                if(!sameTiles) return true;

                            Optional<ItemTile> brokenItem = parentTiles.stream().filter(tile -> !tile.equals(refTile)).findAny();
                            if (brokenItem.isEmpty())
                                return true;

                }
            }
        }

        return false;
    }
}
