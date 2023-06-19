package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the number of cards read from the JSON is not enough to draw the nElements cards
 */
public class NotEnoughCardsException extends Exception{
    /**
     * Constructor of a NotEnoughCardsException
     * @param m details of the exception
     */
    public NotEnoughCardsException(String m){
        super(m);
    }
}
