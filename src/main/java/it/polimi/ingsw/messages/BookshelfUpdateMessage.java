package it.polimi.ingsw.messages;


import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Map;

/**
 * This message transfers updates about the bookshelf of a player
 */
public class BookshelfUpdateMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 772877245873057573L;

    private final ArrayList<ItemTile> insertedTiles;
    private final Map<Coordinates, ItemTile> currentMap;
    private final int column;
    private final String username;

    /**
     * Constructor of a BookshelfUpdateMessage
     * @param insertedTiles list of the new tiles inserted in the bookshelf
     * @param column column number where the new tiles have been inserted 
     * @param currentMap map with the current disposition of the tiles in the coordinates
     * @param username username of the player owning the bookshelf
     */
    public BookshelfUpdateMessage(ArrayList<ItemTile> insertedTiles, int column, Map<Coordinates, ItemTile> currentMap, String username) {
        super(MessageType.BOOKSHELF_UPDATE);
        this.insertedTiles = insertedTiles;
        this.currentMap = currentMap;
        this.column = column;
        this.username = username;
    }

    /**
     *
     * @return the list of the tiles inserted
     */
    public ArrayList<ItemTile> getInsertedTiles() {
        return insertedTiles;
    }

    /**
     *
     * @return the current disposition of the player's bookshelf
     */
    public Map<Coordinates, ItemTile> getCurrentMap() {
        return currentMap;
    }

    /**
     *
     * @return the column where the tiles have been inserted
     */
    public int getColumn() {
        return column;
    }

    /**
     *
     * @return the username of the player that owns the bookshelf
     */
    public String getUsername() {
        return username;
    }
}
