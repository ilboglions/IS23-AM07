package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;


public class GameViewController extends GUIController implements Initializable {
    public static final int SIZE = 9;
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
    public GridPane livingroom_grid;
    public StackPane livingroom_grid_container;


    @Override
    public void postNotification(String title, String desc) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        livingroom_grid.setAlignment(Pos.CENTER);
        //livingroom_grid_container.setPrefSize(SIZE*50, SIZE*50);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Pane pane = new Pane();
               //pane.setStyle("-fx-background-color:red");
                livingroom_grid.add(pane, i, j);
                pane.prefWidthProperty().bind(Bindings.min(livingroom_grid_container.widthProperty().divide(SIZE+1),
                        livingroom_grid_container.heightProperty().divide(SIZE+1)));
                pane.prefHeightProperty().bind(Bindings.min(livingroom_grid_container.widthProperty().divide(SIZE+1),
                        livingroom_grid_container.heightProperty().divide(SIZE+1)));
                //GridPane.setColumnIndex(pane, i);
                //GridPane.setRowIndex(pane,j);
            }
        }
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

    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) {
        for(Map.Entry<Coordinates, ItemTile> entry : livingRoomMap.entrySet()){
            Node cell = getNodeFromGridPane(livingroom_grid,entry.getKey().getColumn() ,entry.getKey().getRow());
            if(cell != null){
                Pane paneCell = (Pane)cell;
                Optional<ImageView> imageOptional = getImageFromTile(entry.getValue());
                if(imageOptional.isPresent()) {
                    ImageView image = imageOptional.get();
                    paneCell.getChildren().add(image);
                    image.fitWidthProperty().bind((paneCell.widthProperty()));
                    image.fitHeightProperty().bind((paneCell.heightProperty()));//paneCell.setCenter(img);
                }
                else{
                    paneCell.getChildren().removeAll();
                }
            }
        }
    }

    private Optional<ImageView> getImageFromTile(ItemTile value) {
        Optional<ImageView> result;
        Random random = new Random();
        int number = random.nextInt(3) +1;
        switch( value ){
            case CAT -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/cat" + number + ".png")).toString()));
            case BOOK -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/book" + number + ".png")).toString()));
            case GAME -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/game" + number + ".png")).toString()));
            case FRAME -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/frame" + number + ".png")).toString()));
            case TROPHY -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/trophy" + number + ".png")).toString()));
            case PLANT -> result = Optional.of(new ImageView(Objects.requireNonNull(GameViewController.class.getResource("/images/itemtiles/plant" + number + ".png")).toString()));
            default -> result = Optional.empty();
        }
        return result;
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null
                    && GridPane.getColumnIndex(node) != null
                    && GridPane.getRowIndex(node) == row
                    && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }
    @FXML
    private void clickGrid(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != livingroom_grid) {
            Node parent = clickedNode.getParent();
            while (parent != livingroom_grid) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);

            if(clickedNode.getStyleClass().isEmpty())
                clickedNode.getStyleClass().add("selectedImage");
            else {
                clickedNode.getStyleClass().clear();
            }
            //System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
        }
    }

    public void drawPlayerInTurn(String userInTurn, String thisUser) {
        livingroom_grid.setDisable(!userInTurn.equals(thisUser));
    }
}
