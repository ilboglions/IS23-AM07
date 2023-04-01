package it.polimi.ingsw.model.tokens;

/**
 * the tokenPoint enums represent the possible values of the tokens placed on the common cards
 */
public enum TokenPoint {

    FIRSTPLAYER(1),
    TWO(2),
    FOUR(4),
    SIX(6),
    EIGHT(8);

    /**
     * the int value of the token
     */
    private final int value;

    /**
     * constructor method used to assign the value to the element
     * @param value the integer value represented in terms of points
     */
    TokenPoint(int value) {
        this.value = value;
    }

    /**
     *
     * @return the value of a specific token
     */
    public int getValue() {
        return value;
    }

}

