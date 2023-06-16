package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.Set;

/**
 * This message transfers the set of the players already in the game
 */
public class AlreadyJoinedPlayersMessage extends NetMessage{

    @Serial
    private static final long serialVersionUID = 3454193155074139889L;
    private final Set<String> alreadyJoinedPlayers;

    /**
     * Constructor of a AlreadyJoinedPlayersMessage
     * @param alreadyJoinedPlayers set of the players already in the game
     */
    public AlreadyJoinedPlayersMessage(Set<String> alreadyJoinedPlayers) {
        super(MessageType.ALREADY_JOINED_PLAYERS);
        this.alreadyJoinedPlayers = alreadyJoinedPlayers;
    }

    /**
     * @return the set of the player already in the game
     */
    public Set<String> getAlreadyJoinedPlayers() {
        return alreadyJoinedPlayers;
    }

}
