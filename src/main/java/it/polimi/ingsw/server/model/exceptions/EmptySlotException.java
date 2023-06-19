package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if a coordinates results to an empty slot
 */
public class EmptySlotException extends Exception {
    /**
     * Constructor of a EmptySlotException
     * @param msg details of the exception
     */
    public EmptySlotException(String msg) {
        super(msg);
    }
}
