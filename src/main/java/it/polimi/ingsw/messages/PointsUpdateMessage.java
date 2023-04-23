package it.polimi.ingsw.messages;

import java.io.Serial;

public class PointsUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4830207241149947680L;

    private final int totalPoints;

    PointsUpdateMessage(String username, int totalPoints) {
        super(username, MessageType.POINTS_UPDATE);
        this.totalPoints = totalPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }
}
