package it.polimi.ingsw.server.model.exceptions;

public class PlayerNotFoundException extends Exception{
    public PlayerNotFoundException() {
        super();
    }

    public PlayerNotFoundException(String msg) {
        super(msg);
    }
}
