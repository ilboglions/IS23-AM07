package it.polimi.ingsw.model.cards;

public class tooManyPlayersException extends Exception {
    public tooManyPlayersException(String message) {
        super(message);
    }
}
