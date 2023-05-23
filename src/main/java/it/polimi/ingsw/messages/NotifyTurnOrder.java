package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.ArrayList;

public class NotifyTurnOrder extends NetMessage{
    @Serial
    private static final long serialVersionUID = -7535841890299116067L;

    private final ArrayList<String> playerOrder;

    public NotifyTurnOrder(ArrayList<String> playerOrder) {
        super(MessageType.NOTIFY_TURN_ORDER);
        this.playerOrder = playerOrder;
    }

    public ArrayList<String> getPlayerOrder() {
        return playerOrder;
    }
}
