package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmGameMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -4175469055000899739L;

    private final Boolean confirmGameCreation;
    private final String errorType;
    private final String details;

    public ConfirmGameMessage(Boolean confirmGameCreation, String errorType, String details) {
        super(MessageType.CONFIRM_GAME);
        this.confirmGameCreation = confirmGameCreation;
        this.errorType = errorType;
        this.details = details;
    }

    public Boolean getConfirmGameCreation() {
        return confirmGameCreation;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getDetails() {
        return details;
    }
}
