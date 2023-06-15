package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the FilePath given is invalid
 */
public class IllegalFilePathException extends Exception{
    /**
     * Constructor of a IllegalFIlePathException
     * @param message details of the exception
     */
    public IllegalFilePathException(String message) {
        super(message);
    }
}
