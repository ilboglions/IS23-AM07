package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serial;
import java.util.ArrayList;

public class TokenUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -8390443874496454175L;

    private final ArrayList<ScoringToken> tokens;
    private final String player;

    public TokenUpdateMessage(ArrayList<ScoringToken> tokens, String player) {
        super(MessageType.TOKEN_UPDATE);
        this.tokens = tokens;
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public ArrayList<ScoringToken> getTokens() {
        return tokens;
    }
}
