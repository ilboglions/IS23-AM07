package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;


public class NadiacentElements extends CommonGoalCard{

    private final int nGroups;
    private final int nElems;

    public NadiacentElements(int nPlayers, String description ,int nGroups, int nElems) throws tooManyPlayersException, NegativeFieldException {
        super(nPlayers, description);
        if( nGroups <= 0 || nElems <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nGroups = nGroups;
        this.nElems = nElems;
    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        int foundGroups;

        foundGroups = 0;
        for( int r  = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++){
                if(bookshelf.getItemTile(new Coordinates(r,c)).isEmpty()) continue;

                ItemTile refTile = bookshelf.getItemTile(new Coordinates(r,c)).get();

                foundGroups = search4Groups(refTile, bookshelf, new Coordinates(r,c)) >= nElems ? foundGroups + 1 : foundGroups;

                if ( foundGroups == this.nGroups)
                    return true;
            }
        }

        return false;
    }


    private int search4Groups( ItemTile refTile, Bookshelf bookshelf, Coordinates refCoords){

        int tmpSum = 0;
        ItemTile nextTile;
        Coordinates tmpCoords;
        // base case
        if( refCoords.getX() > bookshelf.getRows() || refCoords.getY() > bookshelf.getColumns()) return 0;



        // general Case

        //right side
        tmpCoords = new Coordinates( refCoords.getX() + 1 , refCoords.getY());
        if( bookshelf.getItemTile(tmpCoords).isPresent() ){
            nextTile = bookshelf.getItemTile(tmpCoords).get();
            if( nextTile.equals(refTile) )
                tmpSum += 1 + search4Groups(nextTile,bookshelf,tmpCoords);
        }
        //bottom side
        tmpCoords = new Coordinates( refCoords.getX(), refCoords.getY()+1);
        if( bookshelf.getItemTile(tmpCoords).isPresent() ){
            nextTile = bookshelf.getItemTile(tmpCoords).get();
            if( nextTile.equals(refTile) )
                tmpSum += 1 + search4Groups(nextTile,bookshelf,tmpCoords);
        }

        return tmpSum;

    }
}
