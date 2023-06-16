package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message transfers an update in the points of a player
 */
public class PointsUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4830207241149947680L;

    private final int totalPoints;
    private final int addedPoints;

    /**
     *
     * @return the username of the player that received points
     */
    public String getUsername() {
        return username;
    }

    private final String username;

    /**
     * Constructor of a PointsUpdateMessage
     * @param username username of the player that received points
     * @param totalPoints total points of the player
     * @param addedPoints added points
     */
    public PointsUpdateMessage(String username, int totalPoints, int addedPoints) {
        super(MessageType.POINTS_UPDATE);
        this.username = username;
        this.totalPoints = totalPoints;
        this.addedPoints = addedPoints;
    }

    /**
     *
     * @return the total points of the player
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     *
     * @return the added points
     */
    public int getAddedPoints() {
        return addedPoints;
    }
}
