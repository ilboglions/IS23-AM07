package it.polimi.ingsw.client.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUIControllerr {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
