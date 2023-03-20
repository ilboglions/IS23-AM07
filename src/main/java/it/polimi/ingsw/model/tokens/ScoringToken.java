package it.polimi.ingsw.model.tokens;

/**
 * scoring token is a class that represent the token placed on common cards  
 */
public class ScoringToken {
    /**
     * represent the tokenPoint type of the instanced token
     */
    private final TokenPoint scoreValue;

    /**
     * constructor method that assign the TokenPoint to the instanced token
     * @param scoreValue the enum element to be assigned
     */
    public ScoringToken(TokenPoint scoreValue) {
        this.scoreValue = scoreValue;
    }

    /**
     * gets the TokenPoint
     * @return the TokenPoint assigned to the element
     */
    public TokenPoint getScoreValue() {
        return scoreValue;
    }
}
