package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmMoveMessage extends NetMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;

    private final Boolean confirmSelection;


    public ConfirmMoveMessage(Boolean confirmSelection) {
        super(MessageType.CONFIRM_MOVE);
        this.confirmSelection = confirmSelection;

    }

    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

}
