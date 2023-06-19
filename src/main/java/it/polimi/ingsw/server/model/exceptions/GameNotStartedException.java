package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the Game still has to start and a player tries to perform an action
 */
public class GameNotStartedException extends Exception{
    /**
     * Constructor of a GameNotStartedException
     * @param message details of the exception
     */
    public GameNotStartedException(String message) {
        super(message);
    }
}
