package it.polimi.ingsw;

public enum Notifications {
    CONNECTED_SUCCESSFULLY("Connected to the server!", "Choose your username!"),
    ERR_CONNECTION_NO_AVAILABLE("Connection error", "Server at this address is not reachable, trying again soon..."),

    ERR_ALREADY_PLAYING_A_GAME("You are already playing a game!", "You can not join a lobby during a game!"),
    ERR_CONNECTION_NO_LONGER_AVAILABLE("Connection error", "Connection with server no longer available!"),
    JOINED_LOBBY_SUCCESSFULLY("Joined in lobby!","Select either to create or to join a game!"),

    ERR_USERNAME_ALREADY_TAKEN("Nickname already taken!", "Choose another nickname and retry"),
    ERR_INVALID_USERNAME("Invalid username selected!","You can't use this username!"),
    GAME_JOINED_SUCCESSFULLY("Game joined successfully","Waiting the game to start..."),
    GAME_CREATED_SUCCESSFULLY("Game created successfully!","Waiting for others player to join!"),
    GAME_RECONNECTION_SUCCESSFULLY("Welcome back!","Reconnected to your previous game"),
    ERR_GAME_NO_AVAILABLE("No game is available!","Wait for a new game, or create a new one!"),
    ERR_GAME_N_PLAYER_OUT_OF_RANGE("The number of player is out of range!","The game can be played from 2 to 4 players!"),

    TILES_SELECTION_ACCEPTED("Your Selection has been accepted!", "Choose the column to fit the selection!"),
    INVALID_TILES_SELECTION("Your selection is not permitted!","Please, select another set of tiles!"),
    ERR_EMPTY_SLOT_SELECTED("One of the slots selected is empty!","Please, select only not empty tiles!"),
    ERR_GAME_NOT_STARTED("The game has not started yet!","Please, wait that the game starts"),
    ERR_GAME_ENDED("The game has already ended!","You can not perform this action when the game is finished!"),
    NOT_YOUR_TURN("You're not in turn!","You can't perform this action in the turn of another player!"),
    TILES_MOVED_SUCCESSFULLY("Move done!","Your move has been accepted"),
    NO_SPACE_IN_BOOKSHELF_COLUMN("No space left in this column!","Choose another column to fill, or select another set of tiles!"),
    PLAYER_NOT_IN_TURN("it's not your turn!","Please, wait until is your turn to perform this move!"),
    CHAT_SENDER_EQUALS_RECIPIENT("Sender equals recipient!","You can not send a message to yourself!"),
    ERR_PLAYER_NO_JOINED_IN_LOBBY("You have not selected the username!","Please, login before trying to join or create a game" ),
    ERR_INVALID_ACTION("Oak's words echoed... ","\"There's a time and place for everything, but not now.\"");
    private final String title;
    private final String description;

    /**
     * This is a constructor method for a Notificatio
     * @param title title of the notification
     * @param description description of the notification
     */
    Notifications(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     *
     * @return the title of the notification
     */
    public String getTitle(){
        return this.title;
    }

    /**
     *
     * @return the description of the notification
     */
    public String getDescription(){
        return this.description;
    }
}
