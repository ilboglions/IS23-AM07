package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This is an abstract type of message sent as confirmation to some operations
 */
public abstract class ConfirmMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 7117859659363194134L;
    private final String errorType;
    private final String details;

    /**
     * Constructor of a ConfirmMessage
     * @param messageType type of this message
     * @param errorType type of error if there's an error
     * @param details details of the message
     */
    ConfirmMessage(MessageType messageType, String errorType, String details) {
        super(messageType);
        this.details = details;
        this.errorType = errorType;
    }

    /**
     * Getter for the error type
     * @return the error type
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Getter for the details
     * @return the details of the message
     */
    public String getDetails() {
        return details;
    }
}
