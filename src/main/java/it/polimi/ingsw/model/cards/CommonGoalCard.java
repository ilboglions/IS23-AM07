package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.tokens.ScoringToken;
import it.polimi.ingsw.model.tokens.TokenPoint;

import java.util.EmptyStackException;
import java.util.Stack;

public abstract class CommonGoalCard {
    private String description;
    private final Stack<ScoringToken> tokenStack;

    public CommonGoalCard(int nPlayers) throws tooManyPlayersException {
        tokenStack = new Stack<>();

        if(TokenPoint.values().length < nPlayers) throw new tooManyPlayersException("Max excepted players: "+TokenPoint.values().length+" players received: "+nPlayers);
        for(int i= TokenPoint.values().length  - 1; i >TokenPoint.values().length - nPlayers; i--) {
            tokenStack.push( new ScoringToken(TokenPoint.values()[i]));
        }

    }
    public ScoringToken popToken() throws EmptyStackException {
        return tokenStack.pop();
    }

    public String getDescription() {
        return description;
    }
}
