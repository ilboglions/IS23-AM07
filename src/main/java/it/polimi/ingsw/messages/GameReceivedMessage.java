package it.polimi.ingsw.messages;

import java.io.Serial;

public class GameReceivedMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4268960731762666490L;

    private final Boolean errorOccured;

    public GameReceivedMessage(Boolean errorOccured) {
        super(MessageType.GAME_RECEIVED_MESSAGE);
        this.errorOccured = errorOccured;
    }

    public Boolean getErrorOccurred() {
        return errorOccured;
    }
}
