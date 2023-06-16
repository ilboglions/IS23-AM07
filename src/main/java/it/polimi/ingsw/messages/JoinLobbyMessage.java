package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message transfers the request of joining the lobby
 */
public class JoinLobbyMessage extends NetMessage {

    private final String username;
    @Serial
    private static final long serialVersionUID = 7249025317272443385L;

    /**
     * Constructor of a JoinLobbyMessage
     * @param username username of the player joining the lobby
     */
    public JoinLobbyMessage(String username) {
        super(MessageType.JOIN_LOBBY);
        this.username = username;

    }

    /**
     *
     * @return the username of the player joining the lobby
     */
    public String getUsername() {
        return username;
    }



}
