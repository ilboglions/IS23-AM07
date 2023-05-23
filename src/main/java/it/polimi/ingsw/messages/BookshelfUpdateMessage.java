package it.polimi.ingsw.messages;


import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Map;

public class BookshelfUpdateMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 772877245873057573L;

    private final ArrayList<ItemTile> insertedTiles;
    private final Map<Coordinates, ItemTile> currentMap;
    private final int column;
    private final String username;

    public BookshelfUpdateMessage(ArrayList<ItemTile> insertedTiles, int column, Map<Coordinates, ItemTile> currentMap, String username) {
        super(MessageType.BOOKSHELF_UPDATE);
        this.insertedTiles = insertedTiles;
        this.currentMap = currentMap;
        this.column = column;
        this.username = username;
    }

    public ArrayList<ItemTile> getInsertedTiles() {
        return insertedTiles;
    }

    public Map<Coordinates, ItemTile> getCurrentMap() {
        return currentMap;
    }

    public int getColumn() {
        return column;
    }

    public String getUsername() {
        return username;
    }
}
