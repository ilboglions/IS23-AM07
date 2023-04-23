package it.polimi.ingsw.messages;

import java.io.Serial;

public class NewPlayerInGame extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1750489737666428768L;

    private final String newPlayerUsername;

    NewPlayerInGame(String username, String newPlayerUsername) {
        super(username, MessageType.NEW_PLAYER);
        this.newPlayerUsername = newPlayerUsername;
    }

    public String getNewPlayerUsername() {
        return newPlayerUsername;
    }
}
