package it.polimi.ingsw.messages;


import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.util.ArrayList;

public class BookshelfUpdateMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 772877245873057573L;

    private final ArrayList<ItemTile> insertedTiles;
    private final int column;

    BookshelfUpdateMessage(String username, ArrayList<ItemTile> insertedTiles, int column) {
        super(username, MessageType.BOOKSHELF_UPDATE);
        this.insertedTiles = insertedTiles;
        this.column = column;
    }

    public ArrayList<ItemTile> getInsertedTiles() {
        return insertedTiles;
    }

    public int getColumn() {
        return column;
    }
}
