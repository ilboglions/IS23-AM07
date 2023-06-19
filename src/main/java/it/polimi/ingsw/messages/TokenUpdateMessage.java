package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message notifies the client about the update of the tokens owned by a player
 */
public class TokenUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -8390443874496454175L;

    private final ArrayList<ScoringToken> tokens;
    private final String player;

    /**
     * Constructor of a TokenUpdateMessage
     * @param tokens list of the tokens assigned to a player
     * @param player username of the player
     */
    public TokenUpdateMessage(ArrayList<ScoringToken> tokens, String player) {
        super(MessageType.TOKEN_UPDATE);
        this.tokens = tokens;
        this.player = player;
    }

    /**
     *
     * @return the player that received the tokens
     */
    public String getPlayer() {
        return player;
    }

    /**
     *
     * @return the list of the tokens owned by the player
     */
    public ArrayList<ScoringToken> getTokens() {
        return tokens;
    }
}
