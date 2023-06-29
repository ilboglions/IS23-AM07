package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.server.model.utilities.UtilityFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Controller class of the GUI
 */
public class LobbyViewController extends GUIController {
    public Button createGameBtn;
    public Button joinGameBtn;
    public Button login;
    public Button joinGameButton;
    public Button createGameButton;
    public VBox selectionBox;
    public VBox createGameBox;
    public Button confirmCreateButton;
    public TextField nPlayerField;
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private Label errorLabelDesc;


    /**
     * The click on the LoginButton launches a JoinLobby request is launched by the client
     * The username is based on the content of the usernameField TextField.
     * @param actionEvent click event on the button
     */
    public void onLoginButtonClick(ActionEvent actionEvent) {
        try {
            getClientController().JoinLobby(usernameField.getText());
        } catch (RemoteException e) {
            try {
                this.getClientController().close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * The click on the JoinGameButton launches a JoinGame request to the server
     * @param actionEvent click event on the button
     */
    public void onJoinGameButtonClick(ActionEvent actionEvent){
        try {
            getClientController().JoinGame();
        } catch (RemoteException ignored) {

        }
    }

    /**
     *  The click on the CreateGameButton enables the fields for the creation of the game
     * @param actionEvent click event on the button
     */
    public void onCreateGameButtonClick(ActionEvent actionEvent){
        selectionBox.setManaged(false);
        selectionBox.setVisible(false);

        createGameBox.setManaged(true);
        createGameBox.setVisible(true);
    }

    /**
     * The click on the ConfirmCreateGameButton launches a CreateGame request to the server
     * @param actionEvent click event on the button
     */
    public void onConfirmCreateButtonClick(ActionEvent actionEvent){
        try {
            if(UtilityFunctions.isNumeric(nPlayerField.getText())){
                getClientController().CreateGame(Integer.parseInt(nPlayerField.getText()));
            } else {
                this.postNotification("ERROR!","please, insert a number!");
            }

        } catch (RemoteException e) {
            try {
                this.getClientController().close();
            } catch (IOException ignored) {
            }
        }
    }


    /**
     * This method displays a notification
     * @param title title of the notification
     * @param desc description of the notification
     */
    @Override
    public void postNotification(String title, String desc) {
        errorLabel.setText(title);
        errorLabelDesc.setText(desc);
    }
}
