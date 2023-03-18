package it.polimi.ingsw.model.bookshelf.exceptions;

public class NotEnoughSpaceException extends Exception{
    public NotEnoughSpaceException(String message) {
        super(message);
    }
}
