package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public abstract class GUIController {
    private static ConnectionHandler clientController;
    protected Stage stage;

    public void setConnectionHandler( ConnectionHandler clientController){
        GUIController.clientController = clientController;
    }
    public abstract void postNotification(String title, String desc);
    protected ConnectionHandler getClientController(){
        return GUIController.clientController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
