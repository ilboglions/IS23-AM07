package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if we try to assign a token to a player that has already received it
 */
public class TokenAlreadyGivenException extends Exception {
    /**
     * Constructor of a TokenAlreadyGivenException
     */
    public TokenAlreadyGivenException(){
        super();
    }
}
