package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

public class GameViewController extends GUIController implements Initializable {
    public VBox firstPlayerBookshelf;
    public VBox secondPlayerBookshelf;
    public VBox thirdPlayerBookshelf;
    public VBox personalBookshelf;
    public Label personalBookshelfLabel;
    public Label firstPlayerLabel;
    public Label secondPlayerLabel;
    public Label thirdPlayerLabel;
    public TextArea textAreaChat;
    public TextField textFieldChat;
    public Button buttonChatSend;


    @Override
    public void postNotification(String title, String desc) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void drawBookshelf(Map<Coordinates, ItemTile> tilesMap, String playerUsername, int order) {
        VBox currentBookshelf;

        if(order == 0){
            currentBookshelf = personalBookshelf;
            personalBookshelfLabel.setText(playerUsername);
        }
        else if(order == 1) {
            currentBookshelf = firstPlayerBookshelf;
            firstPlayerLabel.setText(playerUsername);
        }
        else if(order == 2) {
            currentBookshelf = secondPlayerBookshelf;
            secondPlayerLabel.setText(playerUsername);
        }
        else {
            currentBookshelf = thirdPlayerBookshelf;
            thirdPlayerLabel.setText(playerUsername);
        }

        if(!currentBookshelf.isVisible()){
            currentBookshelf.setVisible(true);
        }

        //TODO: display the tilesMap on the currentBookshelf
    }

    private VBox getBookshelfFromTurnOrder(int order){
        /*if(order == 0)
            return personalBookshelf;
        else if(order == 1)
            return firstPlayerBookshelf;
        else if(order == 2)
            return secondPlayerBookshelf;
        else
            return thirdPlayerBookshelf;*/

        return personalBookshelf;
    }

    public void sendMessage(ActionEvent actionEvent) {
        this.getClientController().sendMessage(textFieldChat.getText());
        this.textFieldChat.clear();
    }

    public void drawChat(List<String> outputMessages) {
        ArrayList<String> messages = new ArrayList<>(outputMessages);
        Collections.reverse(messages);
        StringBuilder chatContent = new StringBuilder("");

        for(String message : messages){
            chatContent.append(message + "\n");
        }

        textAreaChat.setText(String.valueOf(chatContent));
    }
}
