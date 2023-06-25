package it.polimi.ingsw.client.view.CLI;

import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.client.view.CLI.CliView.*;

/**
 * enum with the types of scene with the rendering attributes (dimensions).
 */

public enum Scenario {
    LOBBY(MAX_HORIZ_TILES_LOBBY,MAX_VERT_TILES_LOBBY, 9, 15, 5, 65,
            new HashMap<>() {{
                put("JoinLobby>>*username*","Join the lobby with the username declared i.e. JoinLobby>>Mariano");
                put("JoinGame>>", "Join an available game");
                put("CreateGame>>*number of players*", "Create a game (min 2, max 4 player) i.e. CreateGame>>2 it creates a game for 2 players");
            }}
            ),
    GAME(MAX_HORIZ_TILES_GAME, MAX_VERT_TILES_GAME, START_R_BOX_NOTIFICATION, START_C_BOX_NOTIFICATION,LENGTH_R_BOX_NOTIFICATION,LENGTH_C_BOX_NOTIFICATION,
            new HashMap<>() {{
                put("GetTiles>>(x,y)","Gets the tiles from the livingroom board (Min 1, Max 3) i.e. GetTiles>>(2,3);(4,5);(0,1)");
                put("MoveTiles>>(x,y)-->*column number*", "After correctly selected the tiles, it moves them in the column of your bookshelf i.e. MoveTiles>>(2,3);(4,5);(0,1)-->3");
                put("Chat>>*message*", "Post a message in the chat, if you want to send a direct message, specify the name before '--' i.e. chat>>UserRecipient--text or chat>>text");
                put("Exit>>", "Exit the game (only if it is finished)");
            }}
    );

    private int rows;
    private int cols;

    private int startRNotifications;
    private int startCNotifications;

    private int lengthRNotifications;
    private int lengthCNotifications;

    private Map<String,String> commands;

    Scenario(int cols, int rows, int startRNotifications, int startCNotifications, int lengthRNotifications, int lengthCNotifications, Map<String, String> commands ){
        this.cols = cols;
        this.rows = rows;
        this.startRNotifications = startRNotifications;
        this.startCNotifications = startCNotifications;
        this.lengthRNotifications = lengthRNotifications;
        this.lengthCNotifications = lengthCNotifications;
        this.commands = new HashMap<>(commands);
    }

    /**
     *
     * @return the number of rows of this scenario
     */
    public int getRows(){
        return this.rows;
    }

    /**
     *
     * @return the number of columns of this scenario
     */
    public int getCols(){
        return this.cols;
    }

    /**
     *
     * @return the starting column for the notifications
     */
    public int getStartCNotifications() {
        return startCNotifications;
    }

    /**
     *
     * @return the starting row for the notifications
     */
    public int getStartRNotifications() {
        return startRNotifications;
    }

    /**
     *
     * @return the length (in row units ) of the notifications
     */

    public int getLengthRNotifications() {
        return lengthRNotifications;
    }

    /**
     *
     * @return the length (in column units) of the notifications
     */
    public int getLengthCNotifications() {
        return this.lengthCNotifications;
    }

    /**
     *
     * @return the map of the available commands and descriptions
     */
    public Map<String,String> getCommands() {
        return new HashMap<>(commands);
    }
}
