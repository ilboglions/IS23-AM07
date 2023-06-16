package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message transfers the tile selection to the server
 */
public class TileSelectionMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -2604249059568150877L;

    private final ArrayList<Coordinates> tiles;

    /**
     * Constructor of a TileSelectionMessage
     * @param tiles List of the coordinates of the tiles to select
     */
    public TileSelectionMessage(ArrayList<Coordinates> tiles) {
        super(MessageType.TILES_SELECTION);
        this.tiles = tiles;
    }

    /**
     *
     * @return the list of the coordinates of the selected tiles
     */
    public ArrayList<Coordinates> getTiles() {
        return tiles;
    }
}
