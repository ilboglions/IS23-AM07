package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmGameMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -4175469055000899739L;

    private final Boolean confirmGameCreation;


    public ConfirmGameMessage(Boolean confirmGameCreation, String errorType, String details) {
        super(MessageType.CONFIRM_GAME,errorType,details);
        this.confirmGameCreation = confirmGameCreation;
    }

    public Boolean getConfirmGameCreation() {
        return confirmGameCreation;
    }

}
