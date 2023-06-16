package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message transfers the request of moving tiles
 */
public class MoveTilesMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1456687816086335736L;

    private final ArrayList<Coordinates> tiles;
    private final int column;

    /**
     * Constructor of a MoveTilesMessage
     * @param tiles list of the tiles that must be moved
     * @param column column to insert the tiles in
     */
    public MoveTilesMessage(ArrayList<Coordinates> tiles, int column) {
        super(MessageType.MOVE_TILES);
        this.tiles = tiles;
        this.column = column;
    }

    /**
     *
     * @return the list of the tiles to be moved
     */
    public ArrayList<Coordinates> getTiles() {
        return tiles;
    }

    /**
     *
     * @return the column where the tiles must be moved
     */
    public int getColumn() {
        return column;
    }
}
