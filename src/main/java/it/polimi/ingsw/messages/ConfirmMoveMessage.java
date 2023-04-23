package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmMoveMessage extends NetMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;

    private final Boolean confirmSelection;
    private final String details;

    ConfirmMoveMessage(String username, Boolean confirmSelection, String details) {
        super(username, MessageType.CONFIRM_MOVE);
        this.confirmSelection = confirmSelection;
        this.details = details;
    }

    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

    public String getDetails() {
        return details;
    }
}
