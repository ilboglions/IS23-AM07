package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.findAdjacentElements;


public class NadjacentElements extends CommonGoalCard{

    private final int nGroups;
    private final int nElems;

    /**
     * Creates a NadjacentElements Card
     * @param nPlayers number of players of the game
     * @param description description of the card
     * @param name type of the card
     * @param nGroups number of groups
     * @param nElems number of elements for each group
     * @throws PlayersNumberOutOfRange if the number of players is less than 2 or greater than 4
     * @throws NegativeFieldException if nGroups is less or equal to 0 or nElems is less or equal to 0
     * @throws RemoteException RMI Exception
     */
    public NadjacentElements(int nPlayers, String description, CommonCardType name,int nGroups, int nElems) throws PlayersNumberOutOfRange, NegativeFieldException, RemoteException {
        super(nPlayers, description, name);
        if( nGroups <= 0 || nElems <= 0 ) throw new NegativeFieldException("can't assign negative paramaters!");
        this.nGroups = nGroups;
        this.nElems = nElems;
    }

    /**
     * verifies if in the bookshelf there's at least nGroups of nElems adjacent tiles.
     * @param bookshelf the player bookshelf to verify
     * @return true if the constraint is verified, false otherwise.
     * @throws NotEnoughSpaceException if nGroups * nElems > size of the bookshelf
     */
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
