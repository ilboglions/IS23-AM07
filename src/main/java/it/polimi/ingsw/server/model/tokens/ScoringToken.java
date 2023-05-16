package it.polimi.ingsw.server.model.tokens;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * scoring token is a class that represent the token placed on common cards
 */
public class ScoringToken implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoringToken that = (ScoringToken) o;
        return scoreValue == that.scoreValue;
    }
}
