package it.polimi.ingsw.model.tokens;

public class ScoringToken {
    private final TokenPoint scoreValue;

    public ScoringToken(TokenPoint value) {
        this.scoreValue = value;
    }

    public TokenPoint getScoreValue() {
        return scoreValue;
    }
}
