package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message updated the client that a new player joined the game
 */
public class NewPlayerInGame extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1750489737666428768L;

    private final String newPlayerUsername;

    /**
     * Constructor of a NewPlayerInGame
     * @param newPlayerUsername username of the new player
     */
    public NewPlayerInGame(String newPlayerUsername) {
        super(MessageType.NEW_PLAYER);
        this.newPlayerUsername = newPlayerUsername;
    }

    /**
     * @return username of the new player
     */
    public String getNewPlayerUsername() {
        return newPlayerUsername;
    }
}
