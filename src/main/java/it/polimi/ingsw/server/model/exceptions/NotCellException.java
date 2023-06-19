package it.polimi.ingsw.server.model.exceptions;

/**
 * *Unused* Exception launched if a cell passed by argument is not a cell
 */
public class NotCellException extends Exception {

    /**
     * Constructor of a NotCellException
     * @param s details of the exception
     */
    public NotCellException(String s) {
        super(s);
    }
}
