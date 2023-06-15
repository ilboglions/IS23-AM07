package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the tiles given are not enough to fill the container
 */
public class NotEnoughTilesException extends Exception {

    /**
     * Constructor of a NotEnoughTilesException
     * @param msg details of the exception
     */
    public NotEnoughTilesException(String msg) {
        super(msg);
    }
}
