package it.polimi.ingsw.client.view.CLI;

import it.polimi.ingsw.client.connection.*;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.isNumeric;

public class CliView implements ViewInterface {

    /**
     * dimension of the game view
     */
    private static final int MAX_VERT_TILES = 80; //rows.
    private static final int MAX_HORIZ_TILES = 170; //cols.
    private static final int START_R_BOX_CARD = 55; //55
    private static final int START_C_BOX_CARD = 5;
    private static final int START_C_BOX_CHAT = 110;
    private static final int LENGTH_R_BOX_CARD = 20;
    private static final int LENGTH_C_BOX_CARD = 100;
    private static final int LENGTH_C_BOX_CHAT = 55;
    private static final int START_R_CHAT = START_R_BOX_CARD + LENGTH_R_BOX_CARD;
    private static final int START_R_BOX_LEADERBOARD = 44;
    private static final int LENGTH_R_BOX_LEADERBOARD = 10;
    private static final int LENGTH_C_BOX_LEADERBOARD = 40;

    private final int START_R_BOX_NOTIFICATION = START_R_BOX_LEADERBOARD;
    private final int START_C_BOX_NOTIFICATION = START_C_BOX_CHAT + 10;
    private final int LENGTH_R_BOX_NOTIFICATION = LENGTH_R_BOX_LEADERBOARD;
    private final int LENGTH_C_BOX_NOTIFICATION = LENGTH_C_BOX_CHAT - 10;
    private final int FIXED_H_MARGIN = 3;
    private final int FIXED_V_MARGIN = 1;
    private static final String SPACE = " ";
    private static final int BASE_TILE_DIM = 3;

    private final ExecutorService inputReaderEx;
    private final ConnectionHandler controller;

    private final Scanner inputScan;
    private final String[][] tiles = new String[MAX_VERT_TILES][MAX_HORIZ_TILES];

    public static void main(String[] args){
        ConnectionType c;
        ViewInterface view;
        if (args.length == 2) {

            c = args[0].equals("TCP") ? ConnectionType.TCP : ConnectionType.RMI;

            //ViewInterface cliView = args[1].equals("CLI") ?  new CliView(c);

            view = new CliView(c);

        } else {
            view = new CliView(ConnectionType.RMI);

        }
    }

    public CliView(ConnectionType connectionType){
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);
        inputScan =  new Scanner(System.in);
        this.fillEmpty();
        inputReaderEx = Executors.newCachedThreadPool();
        inputReaderEx.submit( () -> {
            String cliInput;
            synchronized (inputScan){
                while(true){
                    cliInput = inputScan.nextLine();
                    String finalCliInput = cliInput;
                    inputReaderEx.submit(() -> this.handle(finalCliInput));
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
            /*chat>>UserRecipient--cia<ooo*/
            case "Chat" ->{

                String[] chatMessageArray = specific.split("--");
                if( chatMessageArray.length > 1){
                    controller.sendMessage(chatMessageArray[0],chatMessageArray[1]);
                } else {
                    controller.sendMessage(specific);
                }
            }
            /* createGame>>3*/
            case "CreateGame" -> {
                if(isNumeric(specific)){
                    try {
                        controller.CreateGame(Integer.parseInt(specific));
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            /*JoinGame>>*/
            case "JoinGame" -> {
                try {
                    this.postNotification("Trying to join a game","...");
                    controller.JoinGame();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            /*GetTiles>>(2,3);(4,5);(0,1);*/
            case "GetTiles" -> {

                ArrayList<Coordinates> coordinatesList= this.parseCoordinates(specific);


                try {
                    controller.checkValidRetrieve(coordinatesList);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            case "JoinLobby" -> {
                try {
                    controller.JoinLobby(specific);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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

    public void printAsciiArtTitle(){
        System.out.println(Color.YELLOW.escape());
        System.out.println( "███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗");
        System.out.println( "████╗ ████║╚██╗ ██╔╝    ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝");
        System.out.println( "██╔████╔██║ ╚████╔╝     ███████╗███████║█████╗  ██║     █████╗  ██║█████╗  ");
        System.out.println( "██║╚██╔╝██║  ╚██╔╝      ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝  ");
        System.out.println( "██║ ╚═╝ ██║   ██║       ███████║██║  ██║███████╗███████╗██║     ██║███████╗");
        System.out.println( "╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝");
        System.out.println(Color.RESET.escape()+Color.BLACK_BOLD_BRIGHT.escape());
        System.out.println( Color.WHITE_BOLD_BRIGHT.escape()+"=> Not as good as a "+Color.RED_BOLD.escape()+"MargaraCraft"+Color.WHITE_BOLD_BRIGHT.escape()+" episode, but better than nothing!"+Color.RESET.escape());

        System.out.println();
    }

    private void fillEmpty() {

        tiles[0][0] = Color.RESET.escape()+"╔";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[0][c] = Color.RESET.escape()+"═";
        }

        tiles[0][MAX_HORIZ_TILES - 1] = Color.RESET.escape()+"╗";

        for (int r = 1; r < MAX_VERT_TILES - 1; r++) {
            tiles[r][0] = Color.RESET.escape()+"║";
            tiles[r][MAX_HORIZ_TILES-1] = Color.RESET.escape()+"║";
        }

        tiles[MAX_VERT_TILES - 1][0] = Color.RESET.escape()+"╚";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[MAX_VERT_TILES - 1][c] = Color.RESET.escape()+"═";
        }

        tiles[MAX_VERT_TILES - 1][MAX_HORIZ_TILES - 1] = Color.RESET.escape()+"╝";

    }

    public void drawPersonalCard(Map<Coordinates,ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException {
        int startR = START_R_BOX_CARD + 5;
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

        drawTitle(title, startR, startC, Color.RED_BOLD.escape());

        startCBookshelf = (Math.max(title.length(), nTilesTable.length()) - 14) / 2;
        startCPoints = (Math.max(title.length(), nTilesTable.length()) - nTilesTable.length()) / 2;

        this.drawBookShelf( tilesMap, startR + 2, startC + startCBookshelf);

        for(int i=0; i < nTilesTable.length(); i++){
            tiles[startR + 9][startC + startCPoints + i] = Color.WHITE_BOLD_BRIGHT.escape() + nTilesTable.charAt(i);
            tiles[startR + 11][startC + startCPoints + i] = Color.WHITE_BOLD_BRIGHT.escape() + pointsTable.charAt(i);
        }
        this.plot();
    }

    private int printTruncateText(String text, int startR, int startC, int maxC){
        return this.printTruncateText(text,startR,startC,maxC, Color.WHITE_BOLD.escape());
    }

    private int printTruncateText(String text, int startR, int startC, int maxC, String color){
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

    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException {
        int FIXED_MARGIN = 5;
        int startR = START_R_BOX_CARD + FIXED_MARGIN;
        int startC = START_C_BOX_CARD + 45;
        int currentR;
        int currentC;
        String title = "Common Goal Cards";

        drawTitle(title, startR, startC, Color.RED_BOLD.escape());

        currentR = startR + 2;
        currentC = startC;
        for(RemoteCommonGoalCard card : commonGoalCards){
            tiles[currentR][currentC] = Color.WHITE_BOLD.escape() +  "•";
            tiles[currentR][currentC + 1] = " ";
            currentC += 2;

            currentR += printTruncateText(card.getDescription(), currentR, currentC, START_C_BOX_CARD + LENGTH_C_BOX_CARD - FIXED_MARGIN) + 1;

            currentC = startC;
        }

        this.plot();
    }

    public void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException {
        int[] startingPoints;

        startingPoints = getStartsFromTurnOrder(order);
        for(int i=0; i < playerUsername.length(); i++){
            tiles[startingPoints[0] - 2][startingPoints[1] + i] = Color.RED_BOLD.escape() + playerUsername.charAt(i) ;
        }

        this.drawBookShelf( tilesMap,startingPoints[0], startingPoints[1] );
        this.plot();

    }

    private int[] getStartsFromTurnOrder(int order) {
        int[] startingPoints = new int[2];
        if (order == 0) {
            //This is always the case of the player personal bookshelf
            startingPoints[0] = 45;
            startingPoints[1] = 78;
        } else if (order == 1) {
            startingPoints[0] = 25;
            startingPoints[1] = 22;
        } else if (order == 2) {
            startingPoints[0] = 5;
            startingPoints[1] = 78;
        } else {
            startingPoints[0] = 25;
            startingPoints[1] = 132;
        }

        return startingPoints;
    }

    public void drawChat(List<String> chat){

        int currentRChat = START_R_CHAT;
        int nLines;

        for (String message : chat) {
            //TODO: Questo sotto è da spostare nel metodo in cui ci arriva un NOTIFY_NEW_CHAT
            /*tmp = message;

            if (tmp.getRecipient().isPresent())
                msgToBePrinted = "<" + tmp.getSender() + " -> " + tmp.getRecipient().get() + "> " + tmp.getContent();
            else
                msgToBePrinted = "<" + tmp.getSender() + "> " + tmp.getContent();

             */

            nLines = calculateTextLines(message, START_C_BOX_CHAT + FIXED_H_MARGIN, START_C_BOX_CHAT + LENGTH_C_BOX_CHAT - FIXED_H_MARGIN);

            currentRChat -= nLines;

            if (currentRChat > START_R_BOX_CARD - FIXED_V_MARGIN)
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

    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        int startR = 13;
        int startC = 58;

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
    @Override
    public void drawLeaderboard(Map<String, Integer> scoreBoard) {
        String title = "Leaderboard";
        int H_MARGIN = 3;
        int startR = START_R_BOX_LEADERBOARD + 1;
        int startC = START_C_BOX_CARD + 3;
        int currR;
        String tmp;

        drawTitle(title, startR, startC, Color.RED_BOLD.escape());

        currR = startR + 2;
        for(Map.Entry<String, Integer> entry : scoreBoard.entrySet()){
            tmp = String.format("%2d", entry.getValue()) + " - " + entry.getKey();

            currR += printTruncateText(tmp, currR, startC + 2, START_C_BOX_CARD + LENGTH_C_BOX_LEADERBOARD - H_MARGIN) + 1;
        }
        this.plot();
    }

    @Override
    public void postNotification(String title, String description) {
        clearNotificationBox();
        int spaceNeeded = printTruncateText(title.toUpperCase(),START_R_BOX_NOTIFICATION + FIXED_V_MARGIN,START_C_BOX_NOTIFICATION+FIXED_H_MARGIN, LENGTH_C_BOX_NOTIFICATION - FIXED_H_MARGIN, Color.RED_BOLD_BRIGHT.escape());
        printTruncateText(description,START_R_BOX_NOTIFICATION + FIXED_V_MARGIN + spaceNeeded,START_C_BOX_NOTIFICATION+FIXED_H_MARGIN, Math.min(START_C_BOX_NOTIFICATION - FIXED_H_MARGIN + LENGTH_C_BOX_NOTIFICATION, MAX_HORIZ_TILES - FIXED_H_MARGIN));
        this.plot();
    }

    private void clearNotificationBox(){
        for(int i = START_R_BOX_NOTIFICATION + FIXED_V_MARGIN; i<START_R_BOX_NOTIFICATION + LENGTH_R_BOX_NOTIFICATION; i++){
            for(int j = START_C_BOX_NOTIFICATION + FIXED_H_MARGIN; j < START_C_BOX_NOTIFICATION + LENGTH_C_BOX_NOTIFICATION - FIXED_H_MARGIN; j++){
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
                    return Color.CYAN.escape();
                }
                case TROPHY -> {
                    return Color.BLUE.escape();
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
    public final void plot() {

        for (int r = 0; r < MAX_VERT_TILES; r++) {
            for (int c = 0; c < MAX_HORIZ_TILES; c++) {
                if(tiles[r][c] == null) tiles[r][c] = SPACE;
            }
        }

        /* cards box */
        this.drawBox(START_R_BOX_CARD, START_C_BOX_CARD, LENGTH_R_BOX_CARD, LENGTH_C_BOX_CARD, Color.WHITE_BOLD_BRIGHT.escape());
        /* chat box */
        this.drawBox(START_R_BOX_CARD, START_C_BOX_CHAT, LENGTH_R_BOX_CARD, LENGTH_C_BOX_CHAT, Color.WHITE_BOLD_BRIGHT.escape());
        /* notification box*/
        this.drawBox(START_R_BOX_NOTIFICATION,START_C_BOX_NOTIFICATION, LENGTH_R_BOX_NOTIFICATION, LENGTH_C_BOX_NOTIFICATION,Color.WHITE_BOLD_BRIGHT.escape() );

        /* leaderboard box */
        this.drawBox(START_R_BOX_LEADERBOARD, START_C_BOX_CARD, LENGTH_R_BOX_LEADERBOARD, LENGTH_C_BOX_LEADERBOARD, Color.WHITE_BOLD_BRIGHT.escape());

        this.clearScreen();

        for (int r = 0; r < MAX_VERT_TILES; r++) {
            System.out.println();
            for (int c = 0; c < MAX_HORIZ_TILES; c++) {
                System.out.print(tiles[r][c]);
            }
        }
        System.out.println();
        System.out.flush();
    }

    private void clearScreen(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }



}
