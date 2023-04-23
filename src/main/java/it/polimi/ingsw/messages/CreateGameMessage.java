package it.polimi.ingsw.messages;

import java.io.Serial;

public class CreateGameMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -7527502624377210050L;

    private final int playerNumber;

    CreateGameMessage(String username, int playerNumber) {
        super(username, MessageType.CREATE_GAME);
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
