package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the number of elements required can't fit the dimension of the container (bookshelf)
 */
public class NotEnoughSpaceException extends Exception{
    /**
     * Constrcutor of a NotEnoughSpaceException
     * @param message details of the message
     */
    public NotEnoughSpaceException(String message) {
        super(message);
    }
}
