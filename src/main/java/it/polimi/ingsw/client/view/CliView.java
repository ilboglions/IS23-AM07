package it.polimi.ingsw.client.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.connection.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.isNumeric;

public class CliView implements ViewInterface {

    /**
     * dimension of the game view
     */

    private static final int MAX_VERT_TILES = 40; //rows.
    private static final int MAX_HORIZ_TILES = 30; //cols.

    private final ExecutorService inputReaderEx;
    private final ConnectionHandler controller;

    private final Scanner inputScan;
    String[][] tiles = new String[MAX_VERT_TILES][MAX_HORIZ_TILES];

    public CliView(ConnectionType connectionType){
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);
        inputScan =  new Scanner(System.in);
        fillEmpty();
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
            /*chat>>UserRecipient--ciaooo*/
            case "chat" ->{

                String[] chatMessageArray = specific.split("--");
                if( chatMessageArray.length > 1){
                    controller.sendMessage(chatMessageArray[1],chatMessageArray[0]);
                } else {
                    controller.sendMessage(specific);
                }
            }
            /* createGame>>3*/
            case "createGame" -> {
                if(isNumeric(specific)){
                    try {
                        controller.CreateGame(Integer.parseInt(specific));
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            /*joinGame>>*/
            case "joinGame" -> {
                try {
                    this.postNotification("trying joining the game","...");
                    controller.JoinGame();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            /*getTiles>>(2,3);(4,5);(0,1);*/
            case "getTiles" -> {

                ArrayList<Coordinates> coordinatesList= this.parseCoordinates(specific);


                try {
                    controller.checkValidRetrieve(coordinatesList);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
            /* moveTiles>>(2,3);(4,5);(0,1)-->3 */
            case "moveTiles" -> {
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

    public void printTitle(){
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

        tiles[0][0] = "╔";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[0][c] = "═══";
        }

        tiles[0][MAX_HORIZ_TILES - 1] = "╗";

        for (int r = 1; r < MAX_VERT_TILES - 1; r++) {
            tiles[r][0] = "║";
            for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
                tiles[r][c] = "   ";
            }
            tiles[r][MAX_HORIZ_TILES-1] = "║";
        }

        tiles[MAX_VERT_TILES - 1][0] = "╚";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[MAX_VERT_TILES - 1][c] = "═══";
        }

        tiles[MAX_VERT_TILES - 1][MAX_HORIZ_TILES - 1] = "╝";

    }

    public void drawPersonalCard(Map<Coordinates,ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException {
        int startR = 31;
        int startC = 2;

        // System.out.println(Color.WHITE_BOLD_BRIGHT.escape()+"Here's your"+Color.RED_BOLD.escape()+" personal goal card"+Color.WHITE_BOLD_BRIGHT.escape()+"!\n");
        this.drawBookShelf( tilesMap, startR, startC);
        /* System.out.println(Color.WHITE_BOLD_BRIGHT.escape()+"== points reference ==");

        pointsReference.forEach(
                (key, value) -> System.out.print(key+"->"+value+"pt ")
        ); */

    }

    public void drawYourBookShelf(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException {

        int startR = 22;
        int startC = 12;

       // System.out.println(Color.WHITE_BOLD_BRIGHT.escape()+"Here's "+Color.RED_BOLD.escape()+"your "+Color.WHITE_BOLD_BRIGHT.escape()+"bookshelf");
        this.drawBookShelf( tilesMap, startR, startC);
    }
    public void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException {

        int[] startingPoints;
        int nCelle;
        StringBuilder tempString;
        startingPoints = getStartsFromTurnOrder(order);
        nCelle = playerUsername.length()/3;

        for(int i = 0; i < nCelle - 1 && i*3 < playerUsername.length(); i++){
            tempString = new StringBuilder();
            for(int j = i*3; j < i + 3 && i*3 + j < playerUsername.length(); j++){
                if( j < playerUsername.length()){
                    tempString.append(playerUsername.charAt(j));
                } else {
                    tempString.append(" ");
                }
            }
            tiles[startingPoints[0] - 1][startingPoints[1] + i] = tempString.toString();

        }

        tiles[startingPoints[0] - 1][startingPoints[1]] =Color.RED_BOLD.escape()+playerUsername;
        this.drawBookShelf( tilesMap,startingPoints[0], startingPoints[1] );

    }
    private int[] getStartsFromTurnOrder(int order){
        int[] startingPoints = new int[2];
        if(order == 0){
            startingPoints[0] = 2;
            startingPoints[1] = 12;
        } else if( order == 1){
            startingPoints[0] = 11;
            startingPoints[1] = 22;
        } else {
            startingPoints[0] = 11;
            startingPoints[1] = 2;
        }

        return startingPoints;
    }
    public  void drawChatMessage(String sender, String msg){

        String msgToBePrinted = " * "+sender+": "+msg;
        System.out.print(Color.WHITE_BOLD_BRIGHT.escape()+" ");

        for(int i = 0; i < msgToBePrinted.length() + 2;i++)
            System.out.print("-");

        System.out.println();
        System.out.print("|");

        for(int i = 0; i < msgToBePrinted.length()/2-1;i++)
            System.out.print(" ");

        System.out.print("CHAT");

        for(int i = 0; i < msgToBePrinted.length()/2-1;i++)
         System.out.print(" ");

        System.out.print("|\n ");
        for(int i = 0; i < msgToBePrinted.length() + 2;i++)
            System.out.print("-");
        System.out.println(" * "+Color.RED_BOLD.escape()+sender+Color.WHITE_BOLD_BRIGHT.escape()+": "+msg);
    }

    public void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, int startR, int startC) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        int r,c;
        /* bookshelf settings */
        int bookshelfRows = 6;
        int bookshelfColumns = 5;

        for( c = startC; c < bookshelfColumns + startC; c++){
            tiles[startR][c] = Color.WHITE_BOLD_BRIGHT.escape()+c+"  ";
        }


        for(r = bookshelfRows + startR - 1; r >= startR; r--){
            tiles[r][startC] = Color.WHITE_BOLD_BRIGHT.escape()+r+" "+Color.RESET.escape();
            for( c = startC; c < bookshelfColumns + startC; c++){
                coord = new Coordinates(r - startR,c - startC);
                if (tilesMap.containsKey(coord) )
                    colorTile = getColorFromTileType(tilesMap.get(coord));
                else
                    colorTile = Color.BLACK.escape();
                tiles[r][c] = colorTile+"██ ";
            }
        }


    }

    public void drawLivingRoom(Map<Coordinates, Optional<ItemTile>> livingRoomMap) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        int startR = 10;
        int startC = 10;

        int r;
        int c;
        /* bookshelf settings */
        int livingRoomRows = 9;
        int livingRoomColumns = 9;

        //System.out.println(Color.WHITE_BOLD_BRIGHT.escape()+"== Here's the "+Color.RED_BOLD.escape()+"Living room board"+Color.WHITE_BOLD_BRIGHT.escape()+" =="+Color.RESET.escape());

        for( c = startC; c < livingRoomColumns + startC; c++){
            tiles[startR - 1][c] =  Color.WHITE_BOLD_BRIGHT.escape()+(c-startC)+"  ";
        }

        //System.out.println(Color.RESET.escape());

        for(r = livingRoomRows + startR - 1; r >= startR; r--){
            tiles[r][startC - 1] = Color.WHITE_BOLD_BRIGHT.escape()+(r-startR)+" "+Color.RESET.escape();
            for( c = startC; c < livingRoomColumns + startC; c++){
                coord = new Coordinates(r - startR,c - startC);
                if (livingRoomMap.containsKey(coord) ){
                    if (livingRoomMap.get(coord).isPresent()){
                        colorTile = getColorFromTileType(livingRoomMap.get(coord).get());
                    } else {
                        colorTile = Color.BLACK.escape();
                    }
                    tiles[r][c] =  colorTile+"██ ";
                } else{
                    tiles[r][c] ="   ";
                }
            }
        }

    }

    @Override
    public void postNotification(String title, String description) {
        System.out.println(title);
        System.out.println(description);
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
    public final void plot() {
        System.out.print( Color.GREEN.escape());
        for (int r = 0; r < MAX_VERT_TILES; r++) {
            System.out.println();
            for (int c = 0; c < MAX_HORIZ_TILES; c++) {
                System.out.print(tiles[r][c]);
            }
        }
    }
}
