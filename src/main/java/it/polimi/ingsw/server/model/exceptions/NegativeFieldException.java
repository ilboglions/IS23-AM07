package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if negative values are passed to a method that requires positive arguments
 */
public class NegativeFieldException extends Exception{
    /**
     * Constructor of a NegativeFieldException
     * @param message details of the exception
     */
    public NegativeFieldException(String message) {
        super(message);
    }
}