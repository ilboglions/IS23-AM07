package it.polimi.ingsw.messages;

import java.io.Serial;

public class JoinLobbyMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 7249025317272443385L;

    JoinLobbyMessage(String username) {
        super(username, MessageType.JOIN_LOBBY);
    }
}
