package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the game is ended and a player is trying to perform an action
 */
public class GameEndedException extends Exception {
    /**
     * Constructor of a GameEndedException
     */
    public GameEndedException(){
        super();
    }
}
