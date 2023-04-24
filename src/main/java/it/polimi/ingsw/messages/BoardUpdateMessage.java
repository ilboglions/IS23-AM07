package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.Map;
import java.util.Optional;

public class BoardUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 364273986524620672L;

    private final Map<Coordinates, Optional<ItemTile>> tilesInBoard;

    BoardUpdateMessage(Map<Coordinates, Optional<ItemTile>> tilesInBoard) {
        super(MessageType.BOARD_UPDATE);
        this.tilesInBoard = tilesInBoard;
    }

    public Map<Coordinates, Optional<ItemTile>> getTilesInBoard() {
        return tilesInBoard;
    }
}
