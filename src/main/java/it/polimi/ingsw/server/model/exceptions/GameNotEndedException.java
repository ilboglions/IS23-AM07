package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the game is not ended and the player seek details about an ended game (such as the winner)
 */
public class GameNotEndedException extends Exception{
    /**
     * Constructor of a GameNotEndedException
     * @param message details of the exception
     */
    public GameNotEndedException(String message) {
        super(message);
    }
}
