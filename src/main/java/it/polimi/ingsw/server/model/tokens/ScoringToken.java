package it.polimi.ingsw.server.model.tokens;

import javafx.scene.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * scoring token is a class that represent the token placed on common cards
 */
public class ScoringToken extends Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 7969400589451602800L;
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

    /**
     * This method compares two scoring tokens
     * @param o another object
     * @return true if the object o is a scoring token and the score value is the same as this scoring token
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoringToken that = (ScoringToken) o;
        return scoreValue == that.scoreValue;
    }
}
