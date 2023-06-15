package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if a player tries to join a game but there's no game available in the lobby
 */
public class NoAvailableGameException extends Exception{
    /**
     * Constructor of a NoAvailableGameException
     * @param message details of the exception
     */
    public NoAvailableGameException(String message) {
        super(message);
    }
}
