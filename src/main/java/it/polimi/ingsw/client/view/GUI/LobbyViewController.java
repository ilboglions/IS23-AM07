package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LobbyViewController extends GUIController {
    public Button createGameBtn;
    public Button joinGameBtn;
    @FXML
    private Label notificationLabel;


    @FXML
    protected void onCreateGameButtonClick() {

        notificationLabel.setText("suca CEO");
    }
}
