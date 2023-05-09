package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmMoveMessage extends ConfirmMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;
    private final Boolean confirmSelection;

    public ConfirmMoveMessage(Boolean confirmSelection, String errorType, String details) {
        super(MessageType.CONFIRM_MOVE, errorType, details);
        this.confirmSelection = confirmSelection;


    }
    public Boolean getConfirmSelection() {
        return confirmSelection;
    }


}
