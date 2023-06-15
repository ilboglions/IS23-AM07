package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if the sender and the recipient of a private message are the same user
 */
public class SenderEqualsRecipientException extends Exception {
    /**
     * Constructor of a SenderEqualsRecipientException
     * @param msg details of the exception
     */
    public SenderEqualsRecipientException(String msg) {
        super(msg);
    }
}
