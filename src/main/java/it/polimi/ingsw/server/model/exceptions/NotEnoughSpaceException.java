package it.polimi.ingsw.server.model.exceptions;

public class NotEnoughSpaceException extends Exception{
    public NotEnoughSpaceException(String message) {
        super(message);
    }
}
