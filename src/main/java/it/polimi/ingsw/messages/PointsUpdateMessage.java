package it.polimi.ingsw.messages;

import java.io.Serial;

public class PointsUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4830207241149947680L;

    private final int totalPoints;

    public String getUsername() {
        return username;
    }

    private final String username;

    PointsUpdateMessage(String username, int totalPoints) {
        super(MessageType.POINTS_UPDATE);
        this.username = username;
        this.totalPoints = totalPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

}
