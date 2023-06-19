package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.Map;

/**
 * This message transfers updates concerning the livingRoom board
 */
public class BoardUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 364273986524620672L;

    private final Map<Coordinates, ItemTile> tilesInBoard;

    /**
     * Constructor of a BoardUpdateMessage
     * @param tilesInBoard map with coordinates of the tiles as key and ItemTile as value
     */
    public BoardUpdateMessage(Map<Coordinates, ItemTile> tilesInBoard) {
        super(MessageType.BOARD_UPDATE);
        this.tilesInBoard = tilesInBoard;
    }

    /**
     *
     * @return a map of the Tile in each Coordinates
     */
    public Map<Coordinates, ItemTile> getTilesInBoard() {
        return tilesInBoard;
    }
}
