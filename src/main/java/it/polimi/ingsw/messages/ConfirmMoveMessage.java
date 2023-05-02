package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmMoveMessage extends NetMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;
    private final Boolean confirmSelection;
    private final String errorType;
    private final String details;

    public ConfirmMoveMessage(Boolean confirmSelection, String errorType, String details) {
        super(MessageType.CONFIRM_MOVE);
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
