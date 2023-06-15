package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the number of player is outside the admissible range
 */
public class PlayersNumberOutOfRange extends Exception {
    /**
     * Constructor of a PlayersNumberOutOfRange
     * @param message details of the exception
     */
    public PlayersNumberOutOfRange(String message) {
        super(message);
    }
}
