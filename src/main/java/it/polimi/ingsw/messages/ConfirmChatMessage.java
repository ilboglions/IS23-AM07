package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 4011841211848568521L;

    private final Boolean result;

    private final String errorType;
    private final String details;

    public ConfirmChatMessage(Boolean result, String errorType, String details) {
        super(MessageType.CONFIRM_CHAT);
        this.result = result;
        this.errorType = errorType;
        this.details = details;
    }

    public Boolean getResult() {
        return result;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getDetails() {
        return details;
    }
}
