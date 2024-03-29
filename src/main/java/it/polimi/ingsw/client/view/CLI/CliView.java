package it.polimi.ingsw.client.view.CLI;

import it.polimi.ingsw.client.connection.*;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.utilities.UtilityFunctions;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.isNumeric;

/**
 * Main class for managing the client over CLI
 */
public class CliView implements ViewInterface {

    /**
     * dimension of the game view
     */
    private static final int FIXED_H_MARGIN = 3;
    private static final int FIXED_V_MARGIN = 1;
    protected static int MAX_VERT_TILES_GAME = 55; //rows.
    protected static int MAX_HORIZ_TILES_GAME = 220; //cols.
    protected static int MAX_VERT_TILES_LOBBY = 17; //rows.
    protected static int MAX_HORIZ_TILES_LOBBY = 150; //cols.
    private static final int START_C_BOX_CHAT = 110;
    private static final int LENGTH_R_BOX_CARD = 19;
    private static final int LENGTH_C_BOX_CARD = 100;
    private static final int LENGTH_C_BOX_CHAT = 55;
    private static final int START_R_BOX_CARD = MAX_VERT_TILES_GAME - LENGTH_R_BOX_CARD - 2;
    private static final int START_C_BOX_CARD = 5;
    private static final int START_R_CHAT = START_R_BOX_CARD + LENGTH_R_BOX_CARD;
    private static final int LENGTH_R_BOX_LEADERBOARD = 10;
    private static final int LENGTH_C_BOX_LEADERBOARD = LENGTH_C_BOX_CHAT - 10;
    protected static int LENGTH_R_BOX_NOTIFICATION = 6;
    protected static int START_R_BOX_NOTIFICATION = START_R_BOX_CARD - LENGTH_R_BOX_NOTIFICATION - 1;
    private static final int START_R_BOX_LEADERBOARD = START_R_BOX_NOTIFICATION - LENGTH_R_BOX_LEADERBOARD - 1;
    private static final int START_C_BOX_LEADERBOARD = START_C_BOX_CHAT + 10;
    protected static int START_C_BOX_NOTIFICATION = START_C_BOX_CHAT + 10;
    protected static int LENGTH_C_BOX_NOTIFICATION = LENGTH_C_BOX_LEADERBOARD;
    private static final int START_R_MY_BOOKSHELF = 8;
    private static final int START_C_MY_BOOKSHELF = START_C_BOX_LEADERBOARD + (LENGTH_C_BOX_LEADERBOARD - 14) / 2;
    private static final String SPACE = " ";
    private static final int BASE_TILE_DIM = 3;

    private final ExecutorService inputReaderEx;
    private ConnectionHandler controller;

    private final Scanner inputScan;
    private final String[][] tiles = new String[MAX_VERT_TILES_GAME][MAX_HORIZ_TILES_GAME];
    private final ConnectionType connectionType;

    private Scenario scenario;
    private boolean gameEnded;
    private Map<String,String> commands;

    private final String titleColor = Color.YELLOW_BOLD.escape();

    /**
     * Create an instance of CliView
     * @param connectionType the selected type for the connection, either TCP or RMI
     * @param address the ip address of the server
     * @param port the port of the server
     */
    public CliView(ConnectionType connectionType, String address, int port) {
        gameEnded = false;
        this.connectionType = connectionType;
        this.setScenario(Scenario.LOBBY);
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this, address, port);
        inputScan =  new Scanner(System.in);


        this.plot();
        inputReaderEx = Executors.newCachedThreadPool();
        inputReaderEx.execute( () -> {
            String cliInput;
            synchronized (inputScan){
                while(true){
                    cliInput = inputScan.nextLine();
                    String finalCliInput = cliInput;
                    inputReaderEx.execute(() -> this.handle(finalCliInput));
                }
            }

        } );
    }

    /**
     * Main method of the CliView, this method launches the client connection and displays the interface over CLI
     * @param args network connection preference (--TCP for TCP Connection, everything else will be interpreted as RMI
     */
    public static void main(String[] args){
        ConnectionType c;
        ViewInterface view;
        if (args.length >= 1) {

            c = args[0].equals("--TCP") ? ConnectionType.TCP : ConnectionType.RMI;

                if(args.length >= 3){
                    if(UtilityFunctions.isNumeric(args[2]))
                        view = new CliView(c, args[1], Integer.parseInt(args[2]));
                    else
                        view = new CliView(c);
                } else {
                    view = new CliView(c);
                }
        } else {
            view = new CliView(ConnectionType.RMI);

        }
    }

    /**
     * Constructor of the CliView
     * @param connectionType type of the connection preferred
     */
    public CliView(ConnectionType connectionType){
        gameEnded = false;
        this.setScenario(Scenario.LOBBY);
        this.connectionType = connectionType;
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);

        inputScan =  new Scanner(System.in);
        this.plot();
        inputReaderEx = Executors.newCachedThreadPool();
        inputReaderEx.execute( () -> {
            String cliInput;
            synchronized (inputScan){
                while(true){
                    cliInput = inputScan.nextLine();
                    String finalCliInput = cliInput;
                    inputReaderEx.execute(() -> this.handle(finalCliInput));
                }
            }

        } );
    }


    private void handle(String cliInput) {

        String[] inputArray = cliInput.split(">>");
        String command,specific;
        if(inputArray.length >= 2) {
             command = inputArray[0];
             specific = inputArray[1];
        } else {
            command = inputArray[0];
            specific = "";
        }

        switch (command){
            /*chat>>UserRecipient--ciaooo*/
            case "Chat" ->{
                String[] chatMessageArray = specific.split("--");
                if( chatMessageArray.length > 1){
                    if(chatMessageArray[1].length() > 0)
                        controller.sendMessage(chatMessageArray[0],chatMessageArray[1]);
                } else {
                    if(specific.length() > 0)
                        controller.sendMessage(specific);
                }
            }
            /* createGame>>3*/
            case "CreateGame" -> {
                if(isNumeric(specific)){
                    try {
                        controller.CreateGame(Integer.parseInt(specific));
                    } catch (RemoteException ignored) {
                    }
                }
            }
            /*JoinGame>>*/
            case "JoinGame" -> {
                try {
                    this.postNotification("Trying to join a game","...");
                    controller.JoinGame();
                } catch (RemoteException ignored) {
                }
            }
            /*GetTiles>>(2,3);(4,5);(0,1);*/
            case "GetTiles" -> {

                ArrayList<Coordinates> coordinatesList= this.parseCoordinates(specific);


                try {
                    controller.checkValidRetrieve(coordinatesList);
                } catch (RemoteException ignored) {
                }

            }
            /* MoveTiles>>(2,3);(4,5);(0,1)-->3 */
            case "MoveTiles" -> {
                String[] moveInputs = specific.split("-->");
                if(moveInputs.length < 2 ) return;

                ArrayList<Coordinates> coordinatesList= this.parseCoordinates(moveInputs[0]);

                if(isNumeric(moveInputs[1])){
                    try {
                        controller.moveTiles(coordinatesList,Integer.parseInt(moveInputs[1]));
                    } catch (RemoteException ignored) {
                    }
                }

            }

            case "JoinLobby" -> {
                try {
                    controller.JoinLobby(specific);
                } catch (RemoteException ignored) {

                }
            }
            case "Exit" ->{
                if(!this.gameEnded) return;
                this.backToLobby();
            }
            default -> this.postNotification("Command not found!","the command inserted is invalid!");
        }
    }

    private void setScenario(Scenario s){
        this.scenario = s;
        this.gameEnded = false;
        MAX_HORIZ_TILES_GAME = s.getCols();
        MAX_VERT_TILES_GAME = s.getRows();
        START_R_BOX_NOTIFICATION = s.getStartRNotifications();
        START_C_BOX_NOTIFICATION = s.getStartCNotifications();
        LENGTH_R_BOX_NOTIFICATION = s.getLengthRNotifications();
        LENGTH_C_BOX_NOTIFICATION = s.getLengthCNotifications();
        this.commands = s.getCommands();
        this.fillEmpty();
        this.plot();
    }

    private ArrayList<Coordinates> parseCoordinates(String tilesString){
        ArrayList<Coordinates> coordinatesList = new ArrayList<>();
        String[] tilesArray = tilesString.split(";");
        for( String tile : tilesArray){
            try {
                coordinatesList.add( new Coordinates(tile));
            } catch (InvalidCoordinatesException e) {
                this.postNotification("Coordinates are invalid","");
            }
        }

        return coordinatesList;
    }

    /**
     * Prints the tile of the game as AsciiArt
     */
    public void printAsciiArtTitle(){
        printTruncateText( "███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗", FIXED_V_MARGIN + 1, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗",FIXED_V_MARGIN + 2, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "████╗ ████║╚██╗ ██╔╝    ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝", FIXED_V_MARGIN + 3 , 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "██╔████╔██║ ╚████╔╝     ███████╗███████║█████╗  ██║     █████╗  ██║█████╗  " ,FIXED_V_MARGIN + 4, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "██║╚██╔╝██║  ╚██╔╝      ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝  ",FIXED_V_MARGIN + 5, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "██║ ╚═╝ ██║   ██║       ███████║██║  ██║███████╗███████╗██║     ██║███████╗",FIXED_V_MARGIN + 6, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
        printTruncateText( "╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝",FIXED_V_MARGIN + 7, 10, MAX_HORIZ_TILES_GAME - FIXED_H_MARGIN - 10, Color.YELLOW.escape());
    }

    private void fillEmpty() {

        for(int i = 0; i < MAX_VERT_TILES_GAME; i++){
            Arrays.fill(tiles[i], null);
        }

        tiles[0][0] = Color.RESET.escape()+"╔";
        for (int c = 1; c < MAX_HORIZ_TILES_GAME - 1; c++) {
            tiles[0][c] = Color.RESET.escape()+"═";
        }

        tiles[0][MAX_HORIZ_TILES_GAME - 1] = Color.RESET.escape()+"╗";

        for (int r = 1; r < MAX_VERT_TILES_GAME - 1; r++) {
            tiles[r][0] = Color.RESET.escape()+"║";
            tiles[r][MAX_HORIZ_TILES_GAME -1] = Color.RESET.escape()+"║";
        }

        tiles[MAX_VERT_TILES_GAME - 1][0] = Color.RESET.escape()+"╚";
        for (int c = 1; c < MAX_HORIZ_TILES_GAME - 1; c++) {
            tiles[MAX_VERT_TILES_GAME - 1][c] = Color.RESET.escape()+"═";
        }


        tiles[MAX_VERT_TILES_GAME - 1][MAX_HORIZ_TILES_GAME - 1] = Color.RESET.escape()+"╝";

    }

    /**
     * This method is used to draw the player's personalGoalCard
     * @param card reference to a RemotePersonalGoalCard
     * @throws InvalidCoordinatesException if the coordinates given in the card are invalid
     * @throws RemoteException RMI Exception
     */
    public void drawPersonalCard(RemotePersonalGoalCard card) throws InvalidCoordinatesException, RemoteException{
        Map <Coordinates,ItemTile> tilesMap = card.getCardPattern();
        Map <Integer, Integer> pointsReference = card.getPointsReference();
        int startR = START_R_BOX_CARD + 4;
        int startC = START_C_BOX_CARD + 7;
        int startCBookshelf;
        int startCPoints;
        StringBuilder nTilesTable = new StringBuilder("N tiles :");
        StringBuilder pointsTable = new StringBuilder("N points:");

        String title = "Your Personal Goal Card";

        pointsReference.forEach(
                (key, value) -> {
                    nTilesTable.append(" ").append(key);
                    pointsTable.append(" ").append(value);
                }
        );

        drawTitle(title, startR, startC, titleColor);

        startCBookshelf = (Math.max(title.length(), nTilesTable.length()) - 14) / 2;
        startCPoints = (Math.max(title.length(), nTilesTable.length()) - nTilesTable.length()) / 2;

        if(tilesMap != null) {
            this.drawBookShelf(tilesMap, startR + 2, startC + startCBookshelf);
        }
        for(int i=0; i < nTilesTable.length(); i++){
            tiles[startR + 9][startC + startCPoints + i] = Color.WHITE_BOLD_BRIGHT.escape() + nTilesTable.charAt(i);
            tiles[startR + 11][startC + startCPoints + i] = Color.WHITE_BOLD_BRIGHT.escape() + pointsTable.charAt(i);
        }
        tiles[startR + 11][startC + startCPoints + nTilesTable.length()] = Color.WHITE_BOLD_BRIGHT.escape() + pointsTable.charAt(pointsTable.length() - 1);
        this.plot();
    }

    private int printTruncateText(String text, int startR, int startC, int maxC){
        return this.printTruncateText(text,startR,startC,maxC, Color.WHITE_BOLD.escape());
    }

    private int printTruncateText(String text, int startR, int startC, int maxC, String color){
        if(text == null) return 0;

        int currentC = startC;
        int currentR = startR;
        int nLine = 1;

        for(int i=0; i < text.length(); i++){
            if(currentC == maxC){
                currentC = startC;
                currentR++;
                nLine++;
            }

            tiles[currentR][currentC] = color + text.charAt(i);
            currentC ++;
        }

        return nLine;
    }

    private int calculateTextLines(String text,  int startC, int maxC){
        int currentC = startC;
        int nLine = 1;

        for(int i=0; i < text.length(); i++){
            if(currentC == maxC){
                currentC = startC;
                nLine++;
            }

            currentC ++;
        }

        return nLine;
    }

    /**
     * Draws the commonGoal cards
     * @param commonGoalCards list of all the commonGoalCards
     * @throws RemoteException RMI Exception
     */
    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException {
        int FIXED_MARGIN = 5;
        int startR = START_R_BOX_CARD + FIXED_MARGIN;
        int startC = START_C_BOX_CARD + 45;
        int currentR;
        int currentC;
        String title = "Common Goal Cards";

        drawTitle(title, startR, startC, titleColor);

        currentR = startR + 2;
        currentC = startC;
        String token;
        for(RemoteCommonGoalCard card : commonGoalCards){

            if(!card.getTokenStack().isEmpty()){
                token = String.valueOf(card.getTokenStack().pop().getScoreValue().getValue());
            } else {
                token = " ";
            }
            tiles[currentR][currentC] = Color.WHITE_BOLD.escape() +  "•";
            tiles[currentR][currentC + 1] = " ";
            tiles[currentR][currentC + 3] = Color.RED_BACKGROUND.escape()+" ";
            tiles[currentR][currentC + 4] = token;
            tiles[currentR][currentC + 5] = Color.RED_BACKGROUND.escape()+" "+Color.RESET.escape();
            currentC += 6;
            currentR += printTruncateText(" " + card.getDescription(), currentR, currentC, START_C_BOX_CARD + LENGTH_C_BOX_CARD - FIXED_MARGIN) + 1;

            currentC = startC;
        }

        this.plot();
    }

    /**
     * Draws a player's personalBookshelf
     * @param tilesMap map of the tiles present in the bookshelf (coordinates is the key, tile is the value)
     * @param playerUsername username of the bookshelf's owner
     * @param order order of turn for positioning correctly the bookshelf
     * @throws InvalidCoordinatesException if the coordinates in the tilesMap are invalid
     */
    public void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException {
        int[] startingPoints;

        startingPoints = getStartsFromTurnOrder(order);
        clearBox(startingPoints[0] - 3, startingPoints[1] - 1, startingPoints[0] + 8, startingPoints[1] + 15);
        for(int i=0; i < playerUsername.length(); i++){
            tiles[startingPoints[0] - 2][startingPoints[1] + i] = titleColor + playerUsername.charAt(i) ;
        }

        this.drawBookShelf(tilesMap, startingPoints[0], startingPoints[1]);
        this.plot();
    }

    private int[] getStartsFromTurnOrder(int order) {
        int[] startingPoints = new int[2];

        if (order == 0) {
            //This is always the case of the player personal bookshelf
            startingPoints[0] = START_R_MY_BOOKSHELF;
            startingPoints[1] = START_C_MY_BOOKSHELF;
        } else if (order == 1) {
            startingPoints[0] = 5;
            startingPoints[1] = 80;
        } else if (order == 2) {
            startingPoints[0] = 15;
            startingPoints[1] = 80;
        } else {
            startingPoints[0] = 25;
            startingPoints[1] = 80;
        }

        return startingPoints;
    }

    /**
     * Draws the game chat
     * @param chat list of all the messages in the chat
     */
    public void drawChat(List<String> chat){
        int currentRChat = START_R_CHAT;
        int nLines;

        this.clearBox(START_R_BOX_CARD, START_C_BOX_CHAT, START_R_BOX_CARD + LENGTH_R_BOX_CARD, START_C_BOX_CHAT + LENGTH_C_BOX_CHAT);

        for (String message : chat) {

            nLines = calculateTextLines(message, START_C_BOX_CHAT + FIXED_H_MARGIN, START_C_BOX_CHAT + LENGTH_C_BOX_CHAT - FIXED_H_MARGIN);

            currentRChat -= nLines;

            if (currentRChat > START_R_BOX_CARD - FIXED_V_MARGIN + 2 )
                printTruncateText(message, currentRChat, START_C_BOX_CHAT + FIXED_H_MARGIN, START_C_BOX_CHAT + LENGTH_C_BOX_CHAT - FIXED_H_MARGIN);
            else
                break;
        }
        this.plot();
    }

    private void drawTile(int r, int c, String color){
        tiles[r][c] = color+"█";
        tiles[r][c+1] = "█";
        tiles[r][c+2] = SPACE;
    }

    private void drawTile(int r, int c, String color, int dimension){
        int i,j,k;
        if(dimension == 1){
            this.drawTile(r,c,color);
            return;
        }
        k=0;
        for(  i = 0; i < dimension; i++){
            for( j = 0; j < dimension*(BASE_TILE_DIM-1); j+=BASE_TILE_DIM-1){
                for(k = 0; k < BASE_TILE_DIM-1; k++){
                    tiles[r+i][c+j+k] = color+"█";
                }

            }
            tiles[r+i][c+j+k] = Color.RESET.escape()+SPACE;
        }
    }

    private void drawSpace(int r, int c ,int nSpace){
        for(int i = 0; i < nSpace; i++ ){
            tiles[r][c+i] = SPACE;
        }
    }

    private void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, int startR, int startC) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        int r,c;
        /* bookshelf settings */
        int bookshelfRows = 6;
        int bookshelfColumns = 5;

        for( c = startC; c < bookshelfColumns + startC; c++){
            tiles[startR][c] = Color.WHITE_BOLD_BRIGHT.escape()+c;
            this.drawSpace(startR,c,2);
        }


        for(r = startR + bookshelfRows - 1; r >= startR; r--){
            for( c = startC; c < bookshelfColumns*BASE_TILE_DIM + startC; c+=BASE_TILE_DIM){
                coord = new Coordinates(bookshelfRows - (r-startR) - 1,(c- startC)/BASE_TILE_DIM);
                if (tilesMap.containsKey(coord) )
                    colorTile = getColorFromTileType(tilesMap.get(coord));
                else
                    colorTile = Color.BLACK.escape();
                this.drawTile(r,c,colorTile);
            }
        }


    }
    /**
     * Draws the livingRoom Board
     * @param livingRoomMap map of the tiles present on the board  (coordinates is the key, tile is the value)
     * @throws InvalidCoordinatesException if the coordinates in the map are invalid
     */
    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        int startR = 2;
        int startC = 8;

        int r;
        int c;
        /* bookshelf settings */
        int livingRoomRows = 9;
        int livingRoomColumns = 9;
        int dimension = 2;

        for(r =  startR + livingRoomRows*(dimension+1) - 1; r >= startR; r-=(dimension+1)){
            tiles[r][startC - 1] = Color.WHITE_BOLD_BRIGHT.escape()+(r-startR)/(dimension+1);
            drawSpace(r,startC,2);
            for( c = startC; c < livingRoomColumns*BASE_TILE_DIM*dimension + startC; c+=BASE_TILE_DIM*dimension){

                tiles[startR + 1][c] =  Color.WHITE_BOLD_BRIGHT.escape()+(c-startC)/(BASE_TILE_DIM*dimension);

                coord = new Coordinates((r-startR)/(dimension+1),(c- startC)/(BASE_TILE_DIM*dimension));

                if (livingRoomMap.containsKey(coord) ){
                    if (!livingRoomMap.get(coord).equals(ItemTile.EMPTY)){
                        colorTile = getColorFromTileType(livingRoomMap.get(coord));
                    } else {
                        colorTile = Color.BLACK.escape();
                    }
                    this.drawTile(r,c,colorTile,dimension);
                    tiles[r+dimension][c] = SPACE;
                }
            }
        }
    }

    private void drawTitle(String title, int startR, int startC, String color){
        for(int i=0; i < title.length(); i++){
            tiles[startR][startC + i] = color + title.charAt(i);
        }
    }

    /**
     * Draws the updated leaderboard
     * @param scoreBoard map with the username of the players as the key and the points as value
     */
    @Override
    public void drawLeaderboard(Map<String, Integer> scoreBoard) {
        String title = "Leaderboard";
        int H_MARGIN = 3;
        int startR = START_R_BOX_LEADERBOARD + FIXED_V_MARGIN;
        int startC = START_C_BOX_LEADERBOARD + FIXED_H_MARGIN;
        int currR;
        String tmp;

        this.clearBox(START_R_BOX_LEADERBOARD, START_C_BOX_LEADERBOARD, START_R_BOX_LEADERBOARD + LENGTH_R_BOX_LEADERBOARD, START_C_BOX_LEADERBOARD + LENGTH_C_BOX_LEADERBOARD);

        drawTitle(title, startR, startC, titleColor);

        currR = startR + 2;
        Map<String, Integer> orderedMap = scoreBoard.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for(Map.Entry<String, Integer> entry : orderedMap.entrySet()){
            tmp = String.format("%2d", entry.getValue()) + " - " + entry.getKey();

            currR += printTruncateText(tmp, currR, startC + 2, START_C_BOX_CARD + LENGTH_C_BOX_LEADERBOARD - H_MARGIN) + 1;
        }
        this.plot();
    }

    /**
     * Displays a new player in turn
     * @param userInTurn username of the player in turn
     * @param thisUser username of the local player
     */
    public void drawPlayerInTurn(String userInTurn, String thisUser){
        String text;
        int startR = 3;
        int startC;

        if(userInTurn.equals(thisUser))
            text = "It's your turn";
        else
            text = "Turn of: " + userInTurn;

        startC = START_C_BOX_LEADERBOARD + ((LENGTH_C_BOX_LEADERBOARD - text.length()) / 2);

        clearBox(startR - 1, START_C_BOX_LEADERBOARD - 1, startR + 1, START_C_BOX_LEADERBOARD + LENGTH_C_BOX_LEADERBOARD);
        this.printTruncateText(text,startR,startC, MAX_HORIZ_TILES_GAME -FIXED_H_MARGIN-startC, titleColor);
        this.plot();
    }

    /**
     * Not implemented in CLI
     * Draws the list of players available for private messages on the chat
     * @param players list of the players
     */
    @Override
    public void drawChatPlayersList(ArrayList<String> players) {
        //ONLY THE GUI USES THIS METHOD
    }

    /**
     * Not implemented in CLI
     * Draws the final leaderboard (after the end of the game)
     * @param username username of the winner
     * @param playerPoints map with the username of the players as key, the final score as value
     */
    @Override
    public void drawWinnerLeaderboard(String username, Map<String, Integer> playerPoints) {
        this.gameEnded = true;
        this.postNotification(username+" Wins!","congratulation!");
        this.drawLeaderboard(playerPoints);
    }

    /**
     * Not implemented in CLI
     * Draws the scoring tokens owned by each player
     * @param playerScoringTokens map with the username of the player as key, a list of the token owned as value
     */
    @Override
    public void drawScoringTokens(Map<String, ArrayList<ScoringToken>> playerScoringTokens) {
        //ONLY THE GUI USES THIS METHOD
    }

    /**
     * This method takes back the player to the Lobby
     */
    @Override
    public void backToLobby() {
        String ip = controller.getServerIP();
        int port = controller.getServerPort();
        try {
            controller.close();
        } catch (IOException ignored) {
        }
        this.setScenario(Scenario.LOBBY);
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this, ip, port);

        
    }


    /**
     * This method is called when the game is paused and all the players must be frozen (the chat is not frozen)
     */
    @Override
    public void freezeGame() {
        //ONLY THE GUI USES THIS METHOD
    }

    /**
     * Posts a Game notification
     * @param title title of the notification
     * @param description description of the notification
     */
    @Override
    public void postNotification(String title, String description) {


        if(gameEnded && title.equals(Notifications.TILES_MOVED_SUCCESSFULLY.getTitle()))
            return;

        clearBox(START_R_BOX_NOTIFICATION, START_C_BOX_NOTIFICATION, START_R_BOX_NOTIFICATION + LENGTH_R_BOX_NOTIFICATION, START_C_BOX_NOTIFICATION + LENGTH_C_BOX_NOTIFICATION);
        int spaceNeeded = printTruncateText(title.toUpperCase(),START_R_BOX_NOTIFICATION + FIXED_V_MARGIN,START_C_BOX_NOTIFICATION+FIXED_H_MARGIN, START_C_BOX_NOTIFICATION + LENGTH_C_BOX_NOTIFICATION - FIXED_H_MARGIN, titleColor);
        printTruncateText(description,START_R_BOX_NOTIFICATION + FIXED_V_MARGIN + spaceNeeded,START_C_BOX_NOTIFICATION+FIXED_H_MARGIN, START_C_BOX_NOTIFICATION + LENGTH_C_BOX_NOTIFICATION - FIXED_H_MARGIN );
        this.plot();
    }

    /**
     * Posts a Game notification
     * @param n notification to be displayed
     */
    public void postNotification(Notifications n){
        this.postNotification(n.getTitle(),n.getDescription());
    }

    private void clearBox(int startR, int startC, int maxR, int maxC){
        for(int i = startR + 1; i < maxR; i++){
            for(int j = startC + 1; j < maxC; j++){
                tiles[i][j] = " ";
            }
        }
    }


    private String getColorFromTileType(ItemTile tile){
            switch (tile){
                case CAT -> {
                    return Color.GREEN.escape();
                }
                case BOOK -> {
                    return Color.WHITE.escape();
                }
                case GAME -> {
                    return Color.YELLOW.escape();
                }
                case FRAME -> {
                    return Color.BLUE.escape();
                }
                case TROPHY -> {
                    return Color.CYAN.escape();
                }
                case PLANT -> {
                    return Color.PURPLE.escape();
                }
        }

        return Color.BLACK.escape();
    }

    private void drawVertLine(int startR, int startC, int length, String color){
        for(int r = startR; r < startR + length; r++){
            tiles[r][startC] = color+"│";
        }
    }

    private void drawLine(int startR, int startC , int length, String color){
        for(int c = startC; c < startC + length; c++){
            tiles[startR][c] = color+"─";
        }
    }
    private void drawBox(int startR, int startC, int lengthR, int lengthC, String color){
        this.drawLine(startR, startC+1, lengthC-1, color);
        this.drawLine(startR+lengthR, startC+1, lengthC-1, color);
        this.drawVertLine(startR+1,startC, lengthR-1, color);
        this.drawVertLine(startR+1,startC+lengthC, lengthR-1, color);

        tiles[startR][startC] = color + "┌";
        tiles[startR][startC+lengthC] = color + "┐";
        tiles[startR+lengthR][startC] = color + "└";
        tiles[startR+lengthR][startC+lengthC] = color + "┘";
    }

    /**
     * Renders the game interface
     */
    public final void plot() {

        for (int r = 0; r < MAX_VERT_TILES_GAME; r++) {
            for (int c = 0; c < MAX_HORIZ_TILES_GAME; c++) {
                if(tiles[r][c] == null || tiles[r][c].isEmpty()) tiles[r][c] = SPACE;
            }
        }
        if(this.scenario.equals(Scenario.GAME)){
            this.plotGame();
        } else {
            this.plotLobby();
        }
        this.clearScreen();
        for (int r = 0; r < MAX_VERT_TILES_GAME; r++) {
            System.out.println();
            for (int c = 0; c < MAX_HORIZ_TILES_GAME; c++) {
                System.out.print(tiles[r][c]);
            }
        }
        System.out.println();
        System.out.flush();
    }

    private void plotGame() {
        /* cards box */
        this.drawBox(START_R_BOX_CARD, START_C_BOX_CARD, LENGTH_R_BOX_CARD, LENGTH_C_BOX_CARD, Color.WHITE_BOLD_BRIGHT.escape());
        /* chat box */
        this.drawBox(START_R_BOX_CARD, START_C_BOX_CHAT, LENGTH_R_BOX_CARD, LENGTH_C_BOX_CHAT, Color.WHITE_BOLD_BRIGHT.escape());
        /* notification box*/
        this.drawBox(START_R_BOX_NOTIFICATION,START_C_BOX_NOTIFICATION,LENGTH_R_BOX_NOTIFICATION, LENGTH_C_BOX_NOTIFICATION,Color.WHITE_BOLD_BRIGHT.escape() );
        /* leaderboard box */
        this.drawBox(START_R_BOX_LEADERBOARD, START_C_BOX_LEADERBOARD, LENGTH_R_BOX_LEADERBOARD, LENGTH_C_BOX_LEADERBOARD, Color.WHITE_BOLD_BRIGHT.escape());
        /* help box */
        this.drawBox(1, START_C_BOX_CHAT + LENGTH_C_BOX_CHAT + 5 , MAX_VERT_TILES_GAME - 3 * FIXED_V_MARGIN, LENGTH_C_BOX_LEADERBOARD, Color.WHITE_BOLD_BRIGHT.escape());
        /* chat title */
        this.printTruncateText("~~ CHAT ~~",START_R_BOX_CARD + FIXED_V_MARGIN,START_C_BOX_CHAT + FIXED_H_MARGIN,START_C_BOX_CHAT + LENGTH_C_BOX_CHAT - FIXED_H_MARGIN, Color.YELLOW_BOLD.escape());
        /* */
        this.drawCommandsHelp(1 + FIXED_V_MARGIN,START_C_BOX_CHAT + LENGTH_C_BOX_CHAT + 5 + FIXED_H_MARGIN, MAX_HORIZ_TILES_GAME - 4 -  FIXED_H_MARGIN);

    }

    private void drawCommandsHelp(int startR,int startC,int maxC) {
        int rowSpacer = 2;

        this.printTruncateText("~~ COMMAND LIST ~~", startR, startC, maxC, Color.YELLOW_BOLD.escape());

        for( Map.Entry<String,String> c :commands.entrySet()) {
            tiles[startR + rowSpacer][startC] = "•";
            rowSpacer+= this.printTruncateText(c.getKey() + ": ",startR + rowSpacer, startC + 2 , maxC);
            rowSpacer+= this.printTruncateText(c.getValue(),startR + rowSpacer, startC + 4 , maxC);
            rowSpacer++;
        }
    }

    private void plotLobby(){
        /* notification box*/
        this.drawBox(START_R_BOX_NOTIFICATION,START_C_BOX_NOTIFICATION,LENGTH_R_BOX_NOTIFICATION, LENGTH_C_BOX_NOTIFICATION,Color.WHITE_BOLD_BRIGHT.escape() );
        /* help box */
        this.drawBox(1,START_C_BOX_NOTIFICATION+LENGTH_C_BOX_NOTIFICATION + 10, MAX_VERT_TILES_LOBBY - 3, MAX_HORIZ_TILES_LOBBY - (START_C_BOX_NOTIFICATION+LENGTH_C_BOX_NOTIFICATION + 13) ,Color.WHITE_BOLD_BRIGHT.escape() );
        this.drawCommandsHelp(1 + FIXED_V_MARGIN,START_C_BOX_NOTIFICATION + LENGTH_C_BOX_NOTIFICATION + 10 + FIXED_H_MARGIN, MAX_HORIZ_TILES_LOBBY - 2 - FIXED_H_MARGIN);
        this.printAsciiArtTitle();
    }

    private void clearScreen(){
        try
        {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else
            {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        }
        catch (final Exception ignored) {}
    }

    /**
     * Draw a scene (changes the scene displayed)
     * @param scene type of the scene
     */
    @Override
    public void drawScene(SceneType scene){
        if (scene.equals(SceneType.GAME)) {
            this.setScenario(Scenario.GAME);
        } else {
            this.setScenario(Scenario.LOBBY);
        }
    }
}
