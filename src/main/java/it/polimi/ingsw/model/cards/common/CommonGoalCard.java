package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.tokens.ScoringToken;
import it.polimi.ingsw.model.tokens.TokenPoint;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * CommonGoalCard is an abstract class used to represent the common cards of the game.
 */
public abstract class CommonGoalCard {
    /**
     * description stores the description of the card constraint
     */
    private final String description;
    /**
     * this attribute represent the stack of the ScoringTokens assigned to the common card
     */
    private final Stack<ScoringToken> tokenStack;

    /**
     * The card constructor creates the card and assign the ScoringToken's stack based on the number of players
     * @param nPlayers represents the numbers of players that are playing the game, necessary for the tokens to be assigned at the card
     * @param description it is used for explain the card's constraint
     * @throws PlayersNumberOutOfRange when nPlayers exceed the numbers of the tile, tooManyPlayersException will be thrown
     */
    public CommonGoalCard(int nPlayers , String description) throws PlayersNumberOutOfRange {
        if(description == null) throw new IllegalArgumentException("Description cannot be null");
        if(nPlayers < 2) throw new PlayersNumberOutOfRange("Min excepted players: 2 players received: "+nPlayers);
        tokenStack = new Stack<>();
        this.description = description;
        if(TokenPoint.values().length < nPlayers) throw new PlayersNumberOutOfRange("Max excepted players: "+TokenPoint.values().length+" players received: "+nPlayers);
        for(int i= TokenPoint.values().length  - 1; i >TokenPoint.values().length - nPlayers; i--) {
            tokenStack.push( new ScoringToken(TokenPoint.values()[i]));
        }

    }

    /**
     * Make it possible to get the first ScoringToken on the stack, and remove it from the card
     * @return the token point earned by the Player
     * @throws EmptyStackException if all the tokenPoints have been distributed
     */
    public ScoringToken popToken() throws EmptyStackException {
        return tokenStack.pop();
    }

    /**
     * verifyConstraint it is used to check if the player bookshelf given constraint
     * @param bookshelf the player bookshelf to verify
     * @return true, if the bookshelf passed the verification, false instead
     */
    public boolean verifyConstraint(PlayerBookshelf bookshelf) {
        return true;
    }

    /**
     *
     * @return the card description
     */
    public String getDescription() {
        return description;
    }
}
