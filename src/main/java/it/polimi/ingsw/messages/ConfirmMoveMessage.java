package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmMoveMessage extends NetMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;

    private final Boolean confirmSelection;


    public ConfirmMoveMessage(String username, Boolean confirmSelection) {
        super(username, MessageType.CONFIRM_MOVE);
        this.confirmSelection = confirmSelection;

    }

    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

}
