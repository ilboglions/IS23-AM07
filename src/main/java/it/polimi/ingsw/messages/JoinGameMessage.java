package it.polimi.ingsw.messages;

import java.io.Serial;

public class JoinGameMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -7532758106393537051L;

    JoinGameMessage(String username) {
        super(username, MessageType.JOIN_GAME);
    }
}
