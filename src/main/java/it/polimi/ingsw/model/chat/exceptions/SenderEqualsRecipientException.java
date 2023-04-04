package it.polimi.ingsw.model.chat.exceptions;

public class SenderEqualsRecipientException extends Exception {
    public SenderEqualsRecipientException(String msg) {
        super(msg);
    }
}
