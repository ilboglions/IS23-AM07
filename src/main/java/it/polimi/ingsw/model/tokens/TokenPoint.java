package it.polimi.ingsw.model.tokens;

public enum TokenPoint {
    TWO(2),
    FOUR(4),
    SIX(6),
    EIGHT(8);

    private final int value;

    TokenPoint(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}

