package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message trnasfers to the client the full turn order
 */
public class NotifyTurnOrder extends NetMessage{
    @Serial
    private static final long serialVersionUID = -7535841890299116067L;

    private final ArrayList<String> playerOrder;

    /**
     * Constructor of a NotifyTurnOrder message
     * @param playerOrder list of the players in order of turn
     */
    public NotifyTurnOrder(ArrayList<String> playerOrder) {
        super(MessageType.NOTIFY_TURN_ORDER);
        this.playerOrder = playerOrder;
    }

    /**
     * @return the list of the players in order of turn
     */
    public ArrayList<String> getPlayerOrder() {
        return playerOrder;
    }
}
