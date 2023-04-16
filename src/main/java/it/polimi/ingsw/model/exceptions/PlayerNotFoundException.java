package it.polimi.ingsw.model.exceptions;

public class PlayerNotFoundException extends Exception{
    PlayerNotFoundException() {
        super();
    }

    PlayerNotFoundException( String msg) {
        super(msg);
    }
}
