package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the coordinates passed are invalid
 */
public class InvalidCoordinatesException extends Exception{

    /**
     * Constrcutor of an InvalidCoordinatesException
     * @param msg details of the exception
     */
    public InvalidCoordinatesException(String msg) {
        super(msg);
    }
}
