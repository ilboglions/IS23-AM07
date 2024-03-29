package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;

/**
 * MarioPyramid is a common card type that requires a pattern similar to a pyramid
 */
public class MarioPyramid extends CommonGoalCard{

    /**
     * The card constructor creates the card and assign the ScoringToken's stack based on the number of players
     *
     * @param nPlayers    represents the numbers of players that are playing the game, necessary for the tokens to be assigned at the card
     * @param description it is used for explain the card's constraint
     * @param name the name of the card to distinguish the different types
     * @throws PlayersNumberOutOfRange when nPlayers exceed the numbers of the tile, tooManyPlayersException will be thrown
     * @throws RemoteException RMI Exception
     */
    public MarioPyramid(int nPlayers, String description, CommonCardType name) throws PlayersNumberOutOfRange, RemoteException {
        super(nPlayers, description, name);
    }


    public boolean verifyConstraint(Bookshelf bookshelf) {
        return checkPyramid(true,bookshelf) || checkPyramid(false,bookshelf);
}

    /**
     * a private method used to evaluate if exists a pyramid, starting from the bottom left or bottom right
     * @param reverse used to choose one or another starting point, if reverse is true the inspection starts from bottom right
     * @param bookshelf the bookshelf to analyze
     * @return true, if exists a pyramid in the direction chosen
     */
    private boolean checkPyramid( boolean reverse , Bookshelf bookshelf){
        int refRow;
        int r = 0;
        int c;
        int startColumn;

        if(reverse) startColumn = bookshelf.getColumns() - 1; //choosing column to start with
        else startColumn = 0;
        int startingOffset;

        try{
            while(r < bookshelf.getRows() && bookshelf.getItemTile(new Coordinates(r,startColumn)).isPresent() && !bookshelf.getItemTile(new Coordinates(r,startColumn)).get().equals(ItemTile.EMPTY))
                r++;
            if(r < bookshelf.getRows() - 1)
                return false;
            else if(r==bookshelf.getRows()) {
                refRow = bookshelf.getRows() - 1;
                startingOffset=1;
            }
            else {
                refRow = r;
                startingOffset = 0;
            }
            if(reverse){
                for(c = startColumn - startingOffset; c >= 0 && refRow > 0; c--){
                    if ( !(bookshelf.getItemTile(new Coordinates( refRow , c)).isEmpty() && bookshelf.getItemTile(new Coordinates( refRow - 1 , c)).isPresent()))
                        return false;
                    refRow--;
                }
                return true;
            }

            for(c = startColumn + startingOffset; c < bookshelf.getColumns() && refRow > 0; c++){
                if ( !(bookshelf.getItemTile(new Coordinates( refRow , c)).isEmpty() && bookshelf.getItemTile(new Coordinates( refRow - 1 , c)).isPresent()))
                    return false;
                refRow--;
            }
        }catch (InvalidCoordinatesException e ){
            return false;
        }

       return true;


    }
}
