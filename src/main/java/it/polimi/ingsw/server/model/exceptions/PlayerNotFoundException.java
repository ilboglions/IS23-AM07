package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the player is not found
 */
public class PlayerNotFoundException extends Exception{
    /**
     * Constructor of a PlayerNotFoundException
     * @param msg details of the exception
     */
    public PlayerNotFoundException(String msg) {
        super(msg);
    }
}
