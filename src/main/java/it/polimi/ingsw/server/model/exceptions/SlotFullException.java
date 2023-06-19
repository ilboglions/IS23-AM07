package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the slot where it's required to insert a tile is already full
 */
public class SlotFullException extends Exception{

    /**
     * Constructor of a SlotFullException
     * @param msg details of the exception
     */
    public SlotFullException(String msg) {
        super(msg);
    }
}
