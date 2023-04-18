package it.polimi.ingsw.server.model.exceptions;

public class SenderEqualsRecipientException extends Exception {
    public SenderEqualsRecipientException(String msg) {
        super(msg);
    }
}
