package it.polimi.ingsw.client.view;

public enum Notifications {

    GAME_JOINED_SUCCESSFULLY("Game joined successfully","waiting the game to start..."),
    ERR_GAME_NO_AVAILABLE("No game is available!","Wait for a new game, or create a new one!"),
    TILES_SELECTION_ACCEPTED("Your Selection has been accepted!", "Choose the column to fit the selection!"),
    INVALID_TILES_SELECTION("Your selection is not permitted!","please, select another set of tiles!"),
    ERR_EMPTY_SLOT_SELECTED("One of the slots selected is empty!","please, select only not empty tiles!"),
    ERR_GAME_NOT_STARTED("The game has not started yet!","please, wait that the game starts"),
    ERR_GAME_ENDED("The game has already ended!","you can not perform this action when the game is finished!"),
    NOT_YOUR_TURN("You're not in turn!","you can't perform this action in the turn of another player!"),
    TILES_MOVED_SUCCESSFULLY("Move done!","the move has been accepted"),

    NO_SPACE_IN_BOOKSHELF_COLUMN("No space left in this column!","choose another column to fill, or select another set of tiles!");
    private String title;
    private String description;


    Notifications(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
