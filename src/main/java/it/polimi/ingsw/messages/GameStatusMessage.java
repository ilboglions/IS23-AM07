package it.polimi.ingsw.messages;

import java.io.Serial;

public class GameStatusMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 1522954694829465083L;
    private final GameState state;
    private final String details;
    public GameStatusMessage(GameState state, String details) {
        super(MessageType.GAME_STATUS);
        this.state = state;
        this.details = details;
    }

    public GameState getState() { return this.state; }
    public String getDetails() { return this.details; }
}
