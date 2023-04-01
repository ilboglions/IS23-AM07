package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static it.polimi.ingsw.model.utilities.UtilityFunctions.findAdjacentElements;


public class NadjacentElements extends CommonGoalCard{

    private final int nGroups;
    private final int nElems;

    public NadjacentElements(int nPlayers, String description ,int nGroups, int nElems) throws PlayersNumberOutOfRange, NegativeFieldException {
        super(nPlayers, description);
        if( nGroups <= 0 || nElems <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nGroups = nGroups;
        this.nElems = nElems;
    }

    public boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException{


        if(nGroups * nElems > bookshelf.getColumns()*bookshelf.getRows() ) throw new NotEnoughSpaceException( nGroups+" groups of minimum "+nElems+" tiles can't be found! not enough tile!" );

        Set<Coordinates> visitedCoords = new HashSet<>();
        List<List<Coordinates>> groups = new ArrayList<>();

        try{
            for( int r  = 0; r < bookshelf.getRows(); r++){
                for( int c = 0; c < bookshelf.getColumns(); c++){
                    if(bookshelf.getItemTile(new Coordinates(r,c)).isEmpty()) continue;

                    ItemTile refTile = bookshelf.getItemTile(new Coordinates(r,c)).get();

                    List<Coordinates> adjacencyGroup = findAdjacentElements(bookshelf,r, c,refTile, visitedCoords);
                    if(adjacencyGroup.size() >= nElems){
                        groups.add(adjacencyGroup);
                        if ( groups.size() == this.nGroups)
                            return true;
                    }

                }
            }
        } catch (InvalidCoordinatesException e){
            throw new RuntimeException(e);
        }


        return false;
    }


}