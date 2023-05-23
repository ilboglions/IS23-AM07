package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmGameMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -4175469055000899739L;

    private final Boolean confirmGameCreation;
    private final Boolean confirmJoinedGame;


    public ConfirmGameMessage(Boolean confirmGameCreation, String errorType, String details, Boolean confirmJoinedGame) {
        super(MessageType.CONFIRM_GAME,errorType,details);
        this.confirmGameCreation = confirmGameCreation;
        this.confirmJoinedGame = confirmJoinedGame;
    }

    public Boolean getConfirmGameCreation() {
        return confirmGameCreation;
    }

    public Boolean getConfirmJoinedGame() {
        return confirmJoinedGame;
    }
}
