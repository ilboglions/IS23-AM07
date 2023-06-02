package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.cards.common.CommonCardType;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.net.URL;
import java.rmi.RemoteException;
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
    public ComboBox chatRecipientSelector;
    private final ArrayList<Coordinates> selectedCells = new ArrayList<>();
    public HBox cardsPane;
    public StackPane commonGoal1Pane;
    public StackPane personalGoalPane;
    public StackPane commonGoal2Pane;
    public GridPane cardsGrid;
    public VBox leftvbox;
    public ImageView personalCard;
    public ImageView common1;
    public ImageView common2;

    private Popup commonGoalInfo1, commonGoalInfo2;


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
        cardsGrid.setManaged(false);
        cardsGrid.setVisible(false);
        commonGoalInfo1 = new Popup();
        commonGoalInfo2 = new Popup();
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
        String recipient = (String) chatRecipientSelector.getValue();
        if(recipient == null || recipient.isBlank())
            this.getClientController().sendMessage(textFieldChat.getText());
        else
            this.getClientController().sendMessage(recipient,textFieldChat.getText());

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
        ArrayList<Coordinates> selection;
        Coordinates newCell;
        Node clickedNode = event.getPickResult().getIntersectedNode(); //clickedNode
        if (clickedNode != livingroom_grid) { //find the grid pane
            Node parent = clickedNode.getParent();
            while (parent != livingroom_grid) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }
            if(((Pane)clickedNode).getChildren().size() > 0) { //if it's empty there's nothing to select
                Integer colIndex = GridPane.getColumnIndex(clickedNode); //get coordinates
                Integer rowIndex = GridPane.getRowIndex(clickedNode);
                try {
                    newCell = new Coordinates(rowIndex, colIndex);
                    if (clickedNode.getStyleClass().isEmpty()) {  //not selected yet
                        selection = new ArrayList<>(selectedCells);
                        selection.add(newCell);//candidate new selection
                        //System.out.println("Added new Cell: " + newCell.getRow() + " , " + newCell.getColumn());
                        if (checkValidRetrieve(selection)) { //if selection is valid
                            clickedNode.getStyleClass().add("selectedImage"); //select cell
                            selectedCells.add(newCell);
                        }
                    } else {
                        selectedCells.remove(newCell); //deselect a cell
                        clickedNode.getStyleClass().clear();
                        if(!checkValidRetrieve(selectedCells)){
                            for(Coordinates c: selectedCells){
                                Objects.requireNonNull(getNodeFromGridPane(livingroom_grid, c.getColumn(), c.getRow())).getStyleClass().clear();
                            }
                            selectedCells.clear();
                        }
                    }//create coordinates
                } catch (InvalidCoordinatesException ignored) {}
                //System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
            }
        }
    }

    private boolean checkValidRetrieve(ArrayList<Coordinates> coords){
        boolean result;
        switch (coords.size()) {
            case 1 -> result = checkFreeSide(coords.get(0));
            case 2 ->
                    result = checkFreeSide(coords.get(0)) && checkFreeSide(coords.get(1)) && checkNeighbors(coords.get(0), coords.get(1)) != -1;
            case 3 -> {
                coords.sort((a, b) -> {
                    if (a.getRow() < b.getRow())
                        return -1;
                    if (a.getRow() > b.getRow())
                        return 1;
                    if (a.getColumn() < b.getColumn())
                        return -1;
                    return 1;
                });
                result = this.checkValidRetrieve(new ArrayList<>(coords.subList(0, 2))) &&
                        this.checkValidRetrieve(new ArrayList<>(coords.subList(1, 3))) &&
                        checkNeighbors(coords.get(0), coords.get(1)) == checkNeighbors(coords.get(1), coords.get(2));
            }
            default -> result = false;
        }
        return result;
    }
    private int checkNeighbors(Coordinates a, Coordinates b) {
        //returns the row/column in common between them
        if(a.getColumn() == b.getColumn() && (a.getRow() == b.getRow()-1 || a.getRow() == b.getRow()+1))
            return a.getColumn();
        if (a.getRow() == b.getRow() && (a.getColumn() == b.getColumn()-1 || a.getColumn() == b.getColumn()+1))
            return a.getRow()*10;
        return -1;
    }
    private boolean checkFreeSide(Coordinates coord){
        if(coord != null) {
            Pane top = (Pane)getNodeFromGridPane(livingroom_grid, coord.getColumn(), coord.getRow()-1);
            Pane bottom = (Pane)getNodeFromGridPane(livingroom_grid, coord.getColumn(), coord.getRow()+1);
            Pane left = (Pane)getNodeFromGridPane(livingroom_grid, coord.getColumn()-1, coord.getRow());
            Pane right = (Pane)getNodeFromGridPane(livingroom_grid, coord.getColumn()+1, coord.getRow());
            return this.paneIsEmpty(top) || this.paneIsEmpty(bottom) || this.paneIsEmpty(left) || this.paneIsEmpty(right);
        }
        return false;
    }
    private boolean paneIsEmpty(Pane pane){
        if(pane != null)
            return pane.getChildren().isEmpty();
        return true;
    }


    public void drawPlayerInTurn(String userInTurn, String thisUser) {
        livingroom_grid.setDisable(!userInTurn.equals(thisUser));
    }


    public void drawChatPlayerList(ArrayList<String> players) {
        players.add(0,"");
        chatRecipientSelector.setItems(FXCollections.observableArrayList(players));
        chatRecipientSelector.getSelectionModel().selectFirst();
    }

    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Stack<ScoringToken> tokensStack;
        ImageView tokenImage;
        HBox tokensBox1,tokensBox2;
        cardsGrid.setManaged(true);
        cardsGrid.setVisible(true);
        commonGoal1Pane.getChildren().clear();
        commonGoal2Pane.getChildren().clear();
        commonGoal1Pane.getChildren().add(common1);
        commonGoal2Pane.getChildren().add(common2);
        String url1, url2;
        try {
            CommonCardType type1 = commonGoalCards.get(0).getName();
            CommonCardType type2 = commonGoalCards.get(1).getName();
            url1 = getUrlFromCommonType(type1);
            url2 = getUrlFromCommonType(type2);
            common1.setImage(new Image(url1));
            common2.setImage(new Image(url2));
            common1.setPreserveRatio(true);
            common1.fitWidthProperty().bind(Bindings.min(leftvbox.widthProperty().divide(3.5).subtract(15),leftvbox.heightProperty().divide(4)));
            common2.setPreserveRatio(true);
            common2.fitWidthProperty().bind(Bindings.min(leftvbox.widthProperty().divide(3.5).subtract(15), leftvbox.heightProperty().divide(4)));
            tokensBox1 = new HBox();
            tokensBox2 = new HBox();
            tokensStack = commonGoalCards.get(0).getTokenStack();
            commonGoal1Pane.getChildren().add(tokensBox1);
            tokensBox1.setAlignment(Pos.BOTTOM_CENTER);
            for (ScoringToken t : tokensStack) {
                try {
                    tokenImage = new ImageView(GameViewController.class.getResource("/images/scoringTokens/scoring_" + t.getScoreValue().getValue() + ".jpg").toString());
                    tokensBox1.getChildren().add(tokenImage);
                    tokenImage.setPreserveRatio(true);
                    tokenImage.fitWidthProperty().bind(cardsGrid.widthProperty().divide(20));
                } catch (NullPointerException ignored) {
                }
            }
            tokensStack = commonGoalCards.get(1).getTokenStack();
            commonGoal2Pane.getChildren().add(tokensBox2);
            tokensBox2.setAlignment(Pos.BOTTOM_CENTER);
            for (ScoringToken t : tokensStack) {
                try {
                    tokenImage = new ImageView(GameViewController.class.getResource("/images/scoringTokens/scoring_" + t.getScoreValue().getValue() + ".jpg").toString());
                    tokensBox2.getChildren().add(tokenImage);
                    tokenImage.setPreserveRatio(true);
                    tokenImage.fitWidthProperty().bind(cardsGrid.widthProperty().divide(20));
                } catch (NullPointerException ignored) {
                }
            }
            this.drawCommonGoalPopup(commonGoalInfo1, commonGoalCards.get(0));
            this.drawCommonGoalPopup(commonGoalInfo2, commonGoalCards.get(1));
        } catch (RemoteException ignored ){}
    }

    private void drawCommonGoalPopup(Popup commonGoalInfo, RemoteCommonGoalCard commonCard) throws RemoteException {
        Text description = new Text();
        HBox container = new HBox();
        ImageView cardImage = new ImageView(getUrlFromCommonType(commonCard.getName()));
        commonGoalInfo.getContent().add(container);
        //container.getStyleClass().add("popupCommonCards");
        container.setStyle("-fx-background-image: url('"+ GameViewController.class.getResource("/images/misc/base_pagina2.jpg").toString() +"'); -fx-background-repeat: stretch; -fx-background-position: center; -fx-background-size: auto; -fx-border-radius: 15px;");
        //container.setMaxHeight(100);
        HBox.setMargin(cardImage, new Insets(10, 10, 10, 10));
        description.setText(commonCard.getDescription());
        description.setFill(Color.WHITE);
        description.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        description.wrappingWidthProperty().bind(container.heightProperty());
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(cardImage);
        cardImage.setPreserveRatio(true);
        cardImage.fitHeightProperty().bind(stage.heightProperty().divide(3));
        container.getChildren().add(description);
    }

    private String getUrlFromCommonType(CommonCardType type) {
        String url;
        switch (type) {
            case CORNERS -> url = GameViewController.class.getResource("/images/commonGoalCards/corners.jpg").toString();
            case SAME_TYPE -> url = GameViewController.class.getResource("/images/commonGoalCards/sametype.jpg").toString();
            case X_TILES -> url = GameViewController.class.getResource("/images/commonGoalCards/xtiles.jpg").toString();
            case TWO_TILES -> url = GameViewController.class.getResource("/images/commonGoalCards/twotiles.jpg").toString();
            case FOUR_LINES -> url = GameViewController.class.getResource("/images/commonGoalCards/fourlines.jpg").toString();
            case FOUR_TILES -> url = GameViewController.class.getResource("/images/commonGoalCards/fourtiles.jpg").toString();
            case FIVE_DIAGONAL -> url = GameViewController.class.getResource("/images/commonGoalCards/fivediagonal.jpg").toString();
            case MARIO_PYRAMID -> url = GameViewController.class.getResource("/images/commonGoalCards/pyramid.jpg").toString();
            case SIX_COLUMN_TILES -> url = GameViewController.class.getResource("/images/commonGoalCards/sixcolumntiles.jpg").toString();
            case FOUR_TILES_SQUARE -> url = GameViewController.class.getResource("/images/commonGoalCards/fourtilessquare.jpg").toString();
            case TWO_LINES_DIFFERENT -> url = GameViewController.class.getResource("/images/commonGoalCards/twolinesdifferent.jpg").toString();
            default -> url = GameViewController.class.getResource("/images/commonGoalCards/twocolumndifferent.jpg").toString();
        }
        return url;
    }

    public void drawPersonalCard(int card){
        personalCard.setImage(new Image(Objects.requireNonNull(GameViewController.class.getResource("/images/personalGoalCards/Personal_Goals"+ card +".png").toString())));
        personalGoalPane.setAlignment(Pos.CENTER);
        personalCard.setPreserveRatio(true);
        personalCard.fitHeightProperty().bind(Bindings.min(leftvbox.widthProperty().divide(5), leftvbox.heightProperty().divide(3.5)));
    }

    public void clickCommon1(MouseEvent mouseEvent) {
        if(!commonGoalInfo1.isShowing()){
            commonGoalInfo1.show(this.stage);
        }
        else
            commonGoalInfo1.hide();
    }

    public void clickCommon2(MouseEvent mouseEvent)  {
        if(!commonGoalInfo2.isShowing()){
            commonGoalInfo2.show(this.stage);
        }
        else
            commonGoalInfo2.hide();
    }
}
