package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;

public class MarioPyramid extends CommonGoalCard{

    /**
     * The card constructor creates the card and assign the ScoringToken's stack based on the number of players
     *
     * @param nPlayers    represents the numbers of players that are playing the game, necessary for the tokens to be assigned at the card
     * @param description it is used for explain the card's constraint
     * @throws PlayersNumberOutOfRange when nPlayers exceed the numbers of the tile, tooManyPlayersException will be thrown
     */
    public MarioPyramid(int nPlayers, String description) throws PlayersNumberOutOfRange {
        super(nPlayers, description);
    }

    @Override
    public boolean verifyConstraint(PlayerBookshelf bookshelf) {


        boolean found = checkPyramid(false,bookshelf);
        if(found)
            return true;
        return checkPyramid(true,bookshelf);


}

    /**
     * a private method used to evaluate if exists a pyramid, starting from the bottom left or bottom right
     * @param reverse used to choose one or another starting point, if reverse is true the inspection starts from bottom right
     * @param bookshelf the bookshelf to analyze
     * @return true, if exists a pyramid in the direction chosen
     */
    private boolean checkPyramid( boolean reverse , PlayerBookshelf bookshelf){
        int refRow;
        int r = 0;
        int c;
        int startColumn;

        if(reverse) startColumn = bookshelf.getColumns() - 1;
        else startColumn = 0;

        while(r < bookshelf.getRows() && bookshelf.getItemTile(new Coordinates(r,startColumn)).isPresent()) r++;

        refRow = r;
       if(reverse){
           for(c = startColumn; c >= 0 && refRow >= 0; c--){
               if ( !(bookshelf.getItemTile(new Coordinates( refRow , c)).isEmpty() && bookshelf.getItemTile(new Coordinates( refRow - 1 , c)).isPresent()))
                   return false;
               refRow--;
           }
           return true;
       }

       for(c = 1; c < bookshelf.getColumns() && refRow >= 0; c++){
           if ( !(bookshelf.getItemTile(new Coordinates( refRow , c)).isEmpty() && bookshelf.getItemTile(new Coordinates( refRow - 1 , c)).isPresent()))
               return false;
           refRow--;
       }

       return true;


    }
}
