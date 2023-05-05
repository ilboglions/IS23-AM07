package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmSelectionMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -2537603478256907125L;

    private final Boolean confirmSelection;


    public ConfirmSelectionMessage(Boolean confirmSelection, String errorType, String details) {
        super(MessageType.CONFIRM_SELECTION,errorType,details);
        this.confirmSelection = confirmSelection;

    }
    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

}
