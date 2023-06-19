package it.polimi.ingsw.messages;

import it.polimi.ingsw.GameState;

import java.io.Serial;

/**
 * This message transfers updated about the state of the game
 */
public class GameStatusMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 1522954694829465083L;
    private final GameState state;

    /**
     * Constructor of a GameStatusMessage
     * @param state new state to be transferred
     */
    public GameStatusMessage(GameState state) {
        super(MessageType.GAME_STATUS);
        this.state = state;
    }

    /**
     *
     * @return the state reported
     */
    public GameState getState() { return this.state; }

}
