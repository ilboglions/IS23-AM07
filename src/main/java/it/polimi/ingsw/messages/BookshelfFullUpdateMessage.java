package it.polimi.ingsw.messages;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.Map;

public class BookshelfFullUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 788448996192853106L;

    private final Map<Coordinates, ItemTile> currentMap;
    private final String username;

    public BookshelfFullUpdateMessage(Map<Coordinates, ItemTile> currentMap, String username) {
        super(MessageType.BOOKSHELF_FULL_UPDATE);
        this.currentMap = currentMap;
        this.username = username;
    }

    public Map<Coordinates, ItemTile> getCurrentMap() {
        return currentMap;
    }

    public String getUsername() {
        return username;
    }
}
