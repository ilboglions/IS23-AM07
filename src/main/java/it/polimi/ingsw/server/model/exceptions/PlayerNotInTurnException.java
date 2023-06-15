package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if a player tries to perform an action outside his turn
 */
public class PlayerNotInTurnException extends Exception{
    /**
     * Constructor of a PlayerNotInTurnException
     */
    public PlayerNotInTurnException(){
        super();
    }
}
