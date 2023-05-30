package it.polimi.ingsw.messages;

import it.polimi.ingsw.GameState;

import java.io.Serial;

public class GameStatusMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 1522954694829465083L;
    private final GameState state;

    public GameStatusMessage(GameState state) {
        super(MessageType.GAME_STATUS);
        this.state = state;
    }

    public GameState getState() { return this.state; }

}
