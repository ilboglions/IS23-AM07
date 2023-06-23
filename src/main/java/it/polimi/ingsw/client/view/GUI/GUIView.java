package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import it.polimi.ingsw.client.connection.ConnectionHandlerFactory;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.utilities.UtilityFunctions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Gui Application Class
 */
public class GUIView extends Application implements ViewInterface {
    private ConnectionHandler controller;
    private GUIController guiController;
    private FXMLLoader fxmlLoader;
    private Scene scene;
    private Stage stage;

    private ConnectionType connectionType;

    /**
     * This method starts the gui application
     * @param stage stage reference
     */
    @Override
    public void start(Stage stage) {
        Parameters parameters = getParameters();
        List<String> args = parameters.getRaw();
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        if( args.size() >= 1){
            connectionType = args.get(0).equals("--TCP") ? ConnectionType.TCP : ConnectionType.RMI;
            if (args.size() >= 3 && UtilityFunctions.isNumeric(args.get(2)))
                controller = factory.createConnection(this.connectionType, this, args.get(1), Integer.parseInt(args.get(2)));
            else
                controller = factory.createConnection(this.connectionType, this);
        } else {
            this.connectionType = ConnectionType.RMI;
            controller = factory.createConnection(this.connectionType, this);
        }

        this.stage = stage;
        fxmlLoader = new FXMLLoader(GUIView.class.getResource("/fxml/login-view.fxml"));
        try {
            scene = new Scene(fxmlLoader.load(), 1500, 750);
            scene.getStylesheets().add(GUIView.class.getResource("/fxml/css/game-view.css").toExternalForm());
            scene.getStylesheets().add(GUIView.class.getResource("/fxml/css/lobby-view.css").toExternalForm());

            guiController = fxmlLoader.getController();
            guiController.setConnectionHandler(controller);

            this.stage.setTitle("MyShelfie");
            this.stage.setScene(scene);
            this.stage.show();
            this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    e.consume();
                    Alert closeAlert = new Alert(Alert.AlertType.WARNING);
                    closeAlert.setTitle("MyShelfie Warning");
                    closeAlert.setContentText("Are you sure that you want to close the window?");
                    Optional<ButtonType> result = closeAlert.showAndWait();
                    if(result.isPresent()){
                        if(result.get() == ButtonType.OK){
                            try {
                                controller.close();
                            } catch (IOException ignored) {}
                            System.exit(0);
                        }
                        else{
                            closeAlert.hide();
                        }
                    }
                }
            });
        }
        catch (Exception ignored){}
    }


    /**
     * Since this class extends Application the main method calls the start method.
     * @param args network connection preference, --TCP for TCP, everything else is interpreted as RMI
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is used to draw the player's personalGoalCard
     * @param card reference to a RemotePersonalGoalCard
     * @throws RemoteException RMI Exception
     */
    @Override
    public void drawPersonalCard(RemotePersonalGoalCard card) throws RemoteException{
        int id = card.getID();
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawPersonalCard(id);
        });
    }

    /**
     * Draws a player's personalBookshelf
     * @param tilesMap map of the tiles present in the bookshelf (coordinates is the key, tile is the value)
     * @param playerUsername username of the bookshelf's owner
     * @param order order of turn for positioning correctly the bookshelf
     */
    @Override
    public void drawBookShelf(Map<Coordinates, ItemTile> tilesMap, String playerUsername, int order) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawBookshelf(tilesMap, playerUsername, order);
        });
    }

    /**
     * Draws the livingRoom Board
     * @param livingRoomMap map of the tiles present on the board  (coordinates is the key, tile is the value)
     */
    @Override
    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap)  {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawLivingRoom(livingRoomMap);
        });
    }

    /**
     * Draws the commonGoal cards
     * @param commonGoalCards list of all the commonGoalCards
     */
    @Override
    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawCommonCards(commonGoalCards);
        });

    }


    /**
     * Posts a Game notification
     * @param title title of the notification
     * @param description description of the notification
     */
    @Override
    public void postNotification(String title, String description) {
        Platform.runLater(() -> {
            String desc;
            if(!title.contains("Move Done!")) {
                if(title.contains("Your Selection has been accepted!")){
                    desc = "Drag the tiles in the order you want them to be placed";
                }
                else
                    desc = description;
                GUIController controller = fxmlLoader.getController();
                controller.postNotification(title, desc);
            }
        });

    }

    /**
     * Posts a Game notification
     * @param n notification to be displayed
     */
    @Override
    public void postNotification(Notifications n) {
        this.postNotification(n.getTitle(),n.getDescription());
    }

    /**
     * Draws the updated leaderboard
     * @param playerPoints map with the username of the players as the key and the points as value
     */
    @Override
    public void drawLeaderboard(Map<String, Integer> playerPoints) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawLeaderboard(playerPoints);
        });
    }

    /**
     * Draws the game chat
     * @param outputMessages list of all the messages in the chat
     */
    @Override
    public void drawChat(List<String> outputMessages) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawChat(outputMessages);
        });
    }

    /**
     * Draw a scene (changes the scene displayed)
     * @param sceneType type of the scene
     */
    @Override
    public void drawScene(SceneType sceneType) {
        Platform.runLater(() -> {
            String fxmlPath;

            switch(sceneType){
                case GAME -> fxmlPath = "/fxml/game-view.fxml";
                case LOBBY -> fxmlPath = "/fxml/lobby-view.fxml";
                default -> fxmlPath = "/fxml/login-view.fxml";
            }

            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(GUIView.class.getResource(fxmlPath));

            try {
                //scene = new Scene(fxmlLoader.load());
                Parent newRoot = fxmlLoader.load();
                scene.setRoot(newRoot);
            } catch (IOException e) {
                //TODO: add a way to show error in this case
            }

            guiController = fxmlLoader.getController();
            guiController.setStage(stage);
            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * Displays a new player in turn
     * @param userInTurn username of the player in turn
     * @param thisUser username of the local player
     */
    @Override
    public void drawPlayerInTurn(String userInTurn, String thisUser) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawPlayerInTurn(userInTurn, thisUser);
        });
    }

    /**
     * Draws the list of players available for private messages on the chat
     * @param players list of the players
     */
    @Override
    public void drawChatPlayersList(ArrayList<String> players) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawChatPlayerList(players);
        });
    }

    /**
     * Draws the final leaderboard (after the end of the game)
     * @param playerPoints map with the username of the players as key, the final score as value
     */
    @Override
    public void drawWinnerLeaderboard(Map<String, Integer> playerPoints) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            guiController.setManager(this);
            controller.drawWinnerLeaderboard(playerPoints);
        });
    }

    /**
     * Draws the scoring tokens owned by each player
     * @param playerScoringTokens map with the username of the player as key, a list of the token owned as value
     */
    @Override
    public void drawScoringTokens(Map<String, ArrayList<ScoringToken>> playerScoringTokens) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawScoringTokens(new HashMap<>(playerScoringTokens));
        });
    }

    /**
     * Takes the app back to the lobby resetting the connection to the server
     */
    public void backToLobby(){

        try {
            this.drawScene(SceneType.LOGIN);
            String ip = controller.getServerIP();
            int port = controller.getServerPort();
            controller.close();
            ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
            controller = factory.createConnection(connectionType, this, ip, port);
            guiController = fxmlLoader.getController();
            guiController.setConnectionHandler(controller);
            guiController.setManager(this);
        } catch (IOException e) {
            this.stage.close();
        }
    }

    /**
     * This method is called when the game is paused and all the players must be frozen (the chat is not frozen)
     */
    @Override
    public void freezeGame() {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.freezeGame();
        });
    }
}
