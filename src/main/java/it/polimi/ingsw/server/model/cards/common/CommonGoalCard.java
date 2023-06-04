package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.TokenAlreadyGivenException;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.tokens.TokenPoint;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static it.polimi.ingsw.server.ServerMain.logger;
import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.MAX_PLAYERS;

/**
 * CommonGoalCard is an abstract class used to represent the common cards of the game.
 */
public abstract class CommonGoalCard extends UnicastRemoteObject implements RemoteCommonGoalCard {
    @Serial
    private static final long serialVersionUID = -3210639418443007263L;
    /**
     * description stores the description of the card constraint
     */
    private final String description;

    private final CommonCardType name;

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
    public CommonGoalCard(int nPlayers , String description, CommonCardType name) throws PlayersNumberOutOfRange, NullPointerException, RemoteException {
        super();

        if(nPlayers < 2) throw new PlayersNumberOutOfRange("Min excepted players: 2 players received: "+nPlayers);
        tokenStack = new Stack<>();
        this.description = Objects.requireNonNull(description);
        this.name = Objects.requireNonNull(name);
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
    public abstract boolean verifyConstraint(Bookshelf bookshelf) throws NotEnoughSpaceException;

    /**
     *
     * @return the card description
     */
    public String getDescription() throws RemoteException{
        return description;
    }

    @Override
    public Stack<ScoringToken> getTokenStack() throws RemoteException{
        Stack<ScoringToken> tokens= new Stack<>();
        tokens.addAll(tokenStack);
        return  tokens;
    }

    public CommonCardType getName() throws RemoteException{
        return name;
    }
}
