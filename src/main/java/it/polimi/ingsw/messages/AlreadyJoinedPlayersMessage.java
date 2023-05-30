package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.Set;

public class AlreadyJoinedPlayersMessage extends NetMessage{

    @Serial
    private static final long serialVersionUID = 3454193155074139889L;
    private final Set<String> alreadyJoinedPlayers;

    public AlreadyJoinedPlayersMessage(Set<String> alreadyJoinedPlayers) {
        super(MessageType.ALREADY_JOINED_PLAYERS);
        this.alreadyJoinedPlayers = alreadyJoinedPlayers;
    }

    public Set<String> getAlreadyJoinedPlayers() {
        return alreadyJoinedPlayers;
    }

}
