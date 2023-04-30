package it.polimi.ingsw.messages;

import java.io.Serial;

public class PointsUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4830207241149947680L;

    private final int totalPoints;
    private final int addedPoints;

    public String getUsername() {
        return username;
    }

    private final String username;

    public PointsUpdateMessage(String username, int totalPoints, int addedPoints) {
        super(MessageType.POINTS_UPDATE);
        this.username = username;
        this.totalPoints = totalPoints;
        this.addedPoints = addedPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getAddedPoints() {
        return addedPoints;
    }
}
