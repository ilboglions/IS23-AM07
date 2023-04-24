package it.polimi.ingsw.messages;

import java.io.Serial;

public class JoinLobbyMessage extends NetMessage {

    private final String username;
    @Serial
    private static final long serialVersionUID = 7249025317272443385L;

    public JoinLobbyMessage(String username) {
        super(MessageType.JOIN_LOBBY);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
