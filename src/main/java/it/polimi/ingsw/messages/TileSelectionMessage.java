package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.Serial;
import java.util.ArrayList;

public class TileSelectionMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -2604249059568150877L;

    private final ArrayList<Coordinates> tiles;

    TileSelectionMessage(String username, ArrayList<Coordinates> tiles) {
        super(username, MessageType.TILES_SELECTION);
        this.tiles = tiles;
    }

    public ArrayList<Coordinates> getTiles() {
        return tiles;
    }
}
