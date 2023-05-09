package it.polimi.ingsw.messages;

import java.io.Serial;

public abstract class ConfirmMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 7117859659363194134L;
    private final String errorType;
    private final String details;

    ConfirmMessage(MessageType messageType, String errorType, String details) {
        super(messageType);
        this.details = details;
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getDetails() {
        return details;
    }
}
