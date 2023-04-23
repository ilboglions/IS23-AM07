package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.Serial;
import java.util.ArrayList;

public class MoveTilesMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1456687816086335736L;

    private final ArrayList<Coordinates> tiles;
    private final int column;

    MoveTilesMessage(String username, ArrayList<Coordinates> tiles, int column) {
        super(username, MessageType.MOVE_TILES);
        this.tiles = tiles;
        this.column = column;
    }

    public ArrayList<Coordinates> getTiles() {
        return tiles;
    }

    public int getColumn() {
        return column;
    }
}
