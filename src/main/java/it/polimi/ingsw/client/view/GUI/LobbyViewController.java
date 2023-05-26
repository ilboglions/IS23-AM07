package it.polimi.ingsw.client.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.rmi.RemoteException;

public class LobbyViewController extends GUIController {
    public Button createGameBtn;
    public Button joinGameBtn;
    public Button login;
    public Button joinGameButton;
    public Button createGameButton;
    public Pane selectionPane;
    public Pane createGamePane;
    public Button confirmCreateButton;
    public TextField nPlayerField;
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private Label errorLabelDesc;



    public void onLoginButtonClick(ActionEvent actionEvent) {
        try {
            getClientController().JoinLobby(usernameField.getText());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void onJoinGameButtonClick(ActionEvent actionEvent){
        try {
            getClientController().JoinGame();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCreateGameButtonClick(ActionEvent actionEvent){
        selectionPane.setVisible(false);
        createGamePane.setVisible(true);
    }

    public void onConfirmCreateButtonClick(ActionEvent actionEvent){
        try {
            getClientController().CreateGame(Integer.parseInt(nPlayerField.getText()));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void postNotification(String title, String desc) {
        errorLabel.setText(title);
        errorLabelDesc.setText(desc);
    }
}
