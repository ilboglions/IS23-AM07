package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import javafx.scene.control.Label;

public abstract class GUIController {
    private ConnectionHandler clientController;

    private Label notificationLabel;

    public void setConnectionHandler( ConnectionHandler clientController){
        this.clientController = clientController;
    }

    public void postNotification(String title, String desc){
        notificationLabel.setText(desc);
    }
    protected ConnectionHandler getClientController(){
        return this.clientController;
    }
}
