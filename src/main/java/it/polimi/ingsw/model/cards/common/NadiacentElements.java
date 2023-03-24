package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.HashSet;
import java.util.Set;


public class NadiacentElements extends CommonGoalCard{

    private final int nGroups;
    private final int nElems;

    public NadiacentElements(int nPlayers, String description ,int nGroups, int nElems) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nGroups <= 0 || nElems <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nGroups = nGroups;
        this.nElems = nElems;
    }

    public boolean verifyConstraint(Bookshelf bookshelf){

        int foundGroups;
        Set<Coordinates> visitedCoords = new HashSet<>();
        Set<Coordinates> tmpCoords;
        foundGroups = 0;
        for( int r  = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++){
                if(bookshelf.getItemTile(new Coordinates(r,c)).isEmpty()) continue;

                ItemTile refTile = bookshelf.getItemTile(new Coordinates(r,c)).get();

                if( visitedCoords.contains(new Coordinates(r,c)))  continue;

                tmpCoords = search4Groups(refTile, bookshelf, new Coordinates(r,c));
                visitedCoords.addAll(tmpCoords);
                foundGroups = tmpCoords.size() == nElems ? foundGroups + 1 : foundGroups;

                if ( foundGroups == this.nGroups)
                    return true;
            }
        }

        return false;
    }


    private Set<Coordinates> search4Groups( ItemTile refTile, Bookshelf bookshelf, Coordinates refCoords){

        ItemTile nextTile;
        Coordinates tmpCoords;
        Set<Coordinates> coordSet = new HashSet<>();
        // base case
        if( refCoords.getRow() > bookshelf.getRows() || refCoords.getColumn() > bookshelf.getColumns()) return coordSet;



        // general Case
        coordSet.add(refCoords);
        //right side
        tmpCoords = new Coordinates( refCoords.getRow() + 1 , refCoords.getColumn());
        if( bookshelf.getItemTile(tmpCoords).isPresent() ){
            nextTile = bookshelf.getItemTile(tmpCoords).get();
            if( nextTile.equals(refTile) )

                coordSet.addAll(search4Groups(nextTile,bookshelf,tmpCoords));

        }
        //bottom side
        tmpCoords = new Coordinates( refCoords.getRow(), refCoords.getColumn()+1);
        if( bookshelf.getItemTile(tmpCoords).isPresent() ){
            nextTile = bookshelf.getItemTile(tmpCoords).get();
            if( nextTile.equals(refTile) )
                coordSet.addAll(search4Groups(nextTile,bookshelf,tmpCoords));
        }

        return coordSet;

    }
}
