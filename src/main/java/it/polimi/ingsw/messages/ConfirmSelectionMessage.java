package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmSelectionMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -2537603478256907125L;

    private final Boolean confirmSelection;
    private final String errorType;
    private final String details;

    ConfirmSelectionMessage(String username, Boolean confirmSelection, String errorType, String details) {
        super(username, MessageType.CONFIRM_SELECTION);
        this.confirmSelection = confirmSelection;
        this.errorType = errorType;
        this.details = details;
    }

    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getDetails() {
        return details;
    }
}
