package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.TokenAlreadyGivenException;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.tokens.TokenPoint;

import java.util.*;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.MAX_PLAYERS;

/**
 * CommonGoalCard is an abstract class used to represent the common cards of the game.
 */
public abstract class CommonGoalCard {
    /**
     * description stores the description of the card constraint
     */
    private final String description;

    private final Set<String> PlayersReachedGoal;
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
    public CommonGoalCard(int nPlayers , String description) throws PlayersNumberOutOfRange, NullPointerException{

        if(nPlayers < 2) throw new PlayersNumberOutOfRange("Min excepted players: 2 players received: "+nPlayers);
        tokenStack = new Stack<>();
        this.description = Objects.requireNonNull(description);
        if(MAX_PLAYERS< nPlayers) throw new PlayersNumberOutOfRange("Max excepted players: 4 players received: "+nPlayers);

        if(nPlayers == 2) {
            tokenStack.push(new ScoringToken(TokenPoint.FOUR));
            tokenStack.push(new ScoringToken(TokenPoint.EIGHT));
        }else if(nPlayers == 3){
            tokenStack.push( new ScoringToken(TokenPoint.FOUR));
            tokenStack.push( new ScoringToken(TokenPoint.SIX));
            tokenStack.push( new ScoringToken(TokenPoint.EIGHT));
        } else {
            tokenStack.push( new ScoringToken(TokenPoint.TWO));
            tokenStack.push( new ScoringToken(TokenPoint.FOUR));
            tokenStack.push( new ScoringToken(TokenPoint.SIX));
            tokenStack.push( new ScoringToken(TokenPoint.EIGHT));
        }

        PlayersReachedGoal = new HashSet<>();
    }

    /**
     * Make it possible to get the first ScoringToken on the stack, and remove it from the card
     * @return the token point earned by the Player
     * @throws EmptyStackException if all the tokenPoints have been distributed
     */
    public ScoringToken popTokenTo(String Player) throws EmptyStackException, TokenAlreadyGivenException {

        if(PlayersReachedGoal.contains(Player)) throw new TokenAlreadyGivenException();
        PlayersReachedGoal.add(Player);
        return tokenStack.pop();

    }

    /**
     * verifyConstraint it is used to check if the player bookshelf given constraint
     * @param bookshelf the player bookshelf to verify
     * @return true, if the bookshelf passed the verification, false instead
     */
    public boolean verifyConstraint(PlayerBookshelf bookshelf) throws NotEnoughSpaceException {
        return true;
    }

    /**
     *
     * @return the card description
     */
    public String getDescription() {
        return description;
    }

    protected Stack<ScoringToken> getTokenStack(){
        Stack<ScoringToken> tokens= new Stack<>();
        tokens.addAll(tokenStack);
        return  tokens;
    }
}
