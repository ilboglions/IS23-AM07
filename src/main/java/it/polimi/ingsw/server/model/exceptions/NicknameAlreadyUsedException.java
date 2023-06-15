package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if a user tries to log with a username already used
 */
public class NicknameAlreadyUsedException extends Exception{
    /**
     * Constructor of a NicknameAlreadyUsedException
     * @param message details of the exception
     */
    public NicknameAlreadyUsedException(String message) {
        super(message);
    }
}
