package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import it.polimi.ingsw.client.connection.ConnectionHandlerFactory;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIView extends Application implements ViewInterface {
    private ConnectionHandler controller;
    private GUIController guiController;
    private FXMLLoader fxmlLoader;
    private Scene scene;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parameters parameters = getParameters();
        List<String> args = parameters.getRaw();
        ConnectionType connectionType;
        if ( args.size() == 1) {
            connectionType = args.get(0).equals("--TCP") ? ConnectionType.TCP : ConnectionType.RMI;
        } else {
            connectionType = ConnectionType.TCP;
        }

        this.stage = stage;

        fxmlLoader = new FXMLLoader(GUIView.class.getResource("/fxml/login-view.fxml"));

        scene = new Scene(fxmlLoader.load(), 1500, 750);

        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);

        guiController = fxmlLoader.getController();
        guiController.setConnectionHandler(controller);

        this.stage.setTitle("MyShelfie");
        this.stage.setScene(scene);
        this.stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void drawPersonalCard(RemotePersonalGoalCard card) throws RemoteException{
        int id = card.getID();
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawPersonalCard(id);
        });
    }

    @Override
    public void drawBookShelf(Map<Coordinates, ItemTile> tilesMap, String playerUsername, int order) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawBookshelf(tilesMap, playerUsername, order);
        });
    }

    @Override
    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap)  {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawLivingRoom(livingRoomMap);
        });
    }

    @Override
    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawCommonCards(commonGoalCards);
        });

    }


    @Override
    public void postNotification(String title, String description) {
        Platform.runLater(() -> {
            GUIController controller = fxmlLoader.getController();
            controller.postNotification(title, description);
        });

    }

    @Override
    public void postNotification(Notifications n) {
        this.postNotification(n.getTitle(),n.getDescription());
    }

    @Override
    public void drawLeaderboard(Map<String, Integer> playerPoints) {
    }

    @Override
    public void drawChat(List<String> outputMessages) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawChat(outputMessages);
        });
    }

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

            stage.setScene(scene);
            stage.show();
        });
    }

    @Override
    public void drawPlayerInTurn(String userInTurn, String thisUser) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawPlayerInTurn(userInTurn, thisUser);
        });
    }

    @Override
    public void drawChatPlayersList(ArrayList<String> players) {
        Platform.runLater(() -> {
            GameViewController controller = fxmlLoader.getController();
            controller.drawChatPlayerList(players);
        });
    }
}
