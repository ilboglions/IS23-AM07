package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmSelectionMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -2537603478256907125L;

    private final Boolean confirmSelection;


    public ConfirmSelectionMessage(String username, Boolean confirmSelection) {
        super(username, MessageType.CONFIRM_SELECTION);
        this.confirmSelection = confirmSelection;

    }
    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

}
