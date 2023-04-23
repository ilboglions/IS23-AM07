package it.polimi.ingsw.client.view;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Map;
import java.util.Optional;

public class CliView {

    /**
     * colors used for cli prints
     */
    // Reset
    private final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    private final String BLACK = "\033[0;30m";   // BLACK
    private final String RED = "\033[0;31m";     // RED
    private final String GREEN = "\033[0;32m";   // GREEN
    private final String YELLOW = "\033[0;33m";  // YELLOW
    private final String BLUE = "\033[0;34m";    // BLUE
    private final String PURPLE = "\033[0;35m";  // PURPLE
    private final String CYAN = "\033[0;36m";    // CYAN
    private final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    private final String BLACK_BOLD = "\033[1;30m";  // BLACK
    private final String RED_BOLD = "\033[1;31m";    // RED
    private final String GREEN_BOLD = "\033[1;32m";  // GREEN
    private final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    private final String BLUE_BOLD = "\033[1;34m";   // BLUE
    private final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    private final String CYAN_BOLD = "\033[1;36m";   // CYAN
    private final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    private final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    private final String RED_UNDERLINED = "\033[4;31m";    // RED
    private final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    private final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    private final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    private final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    private final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    private final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    private final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    private final String RED_BACKGROUND = "\033[41m";    // RED
    private final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    private final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    private final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    private final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    private final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    private final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    private final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    private final String RED_BRIGHT = "\033[0;91m";    // RED
    private final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    private final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    private final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    private final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    private final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    private final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    private final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    private final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    private final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    private final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    private final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    private final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    private final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    private final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    private final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    private final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    private final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    private final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    private final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    private final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    private final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    private final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE


    public void printTitle(){
        System.out.println(YELLOW);
        System.out.println( "███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗");
        System.out.println( "████╗ ████║╚██╗ ██╔╝    ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝");
        System.out.println( "██╔████╔██║ ╚████╔╝     ███████╗███████║█████╗  ██║     █████╗  ██║█████╗  ");
        System.out.println( "██║╚██╔╝██║  ╚██╔╝      ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝  ");
        System.out.println( "██║ ╚═╝ ██║   ██║       ███████║██║  ██║███████╗███████╗██║     ██║███████╗");
        System.out.println( "╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝");
        System.out.println(RESET+BLACK_BOLD_BRIGHT);
        System.out.println( WHITE_BOLD_BRIGHT+"=> Not as good as a "+RED_BOLD+"MargaraCraft"+WHITE_BOLD_BRIGHT+" episode, but better than nothing!"+RESET);

        System.out.println();
    }

    public void printPersonalCard(Map<Coordinates,ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException {
        System.out.println(WHITE_BOLD_BRIGHT+"Here's your"+RED_BOLD+" personal goal card"+WHITE_BOLD_BRIGHT+"!\n");
        this.printBookShelf( tilesMap);
        System.out.println(WHITE_BOLD_BRIGHT+"== points reference ==");

        pointsReference.forEach(
                (key, value) -> {
            System.out.print(key+"->"+value+"pt ");
        }
        );
    }


    public void printYourBookShelf(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException {
        System.out.println(WHITE_BOLD_BRIGHT+"Here's "+RED_BOLD+"your "+WHITE_BOLD_BRIGHT+"bookshelf");
        this.printBookShelf( tilesMap);
    }
    public void printBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername) throws InvalidCoordinatesException {
        System.out.println(WHITE_BOLD_BRIGHT+"Here's the bookshelf of "+RED_BOLD+playerUsername);
        this.printBookShelf( tilesMap);
    }

    public void printBookShelf(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;
        /* bookshelf settings */
        int bookshelfRows = 6;
        int bookshelfColumns = 5;

        System.out.print("  ");
        for(int c = 0; c < bookshelfColumns; c++){
            System.out.print(WHITE_BOLD_BRIGHT+c+"  ");
        }

        System.out.println(RESET);

        for(int r = bookshelfRows - 1; r >= 0; r--){
            System.out.print(WHITE_BOLD_BRIGHT+r+" "+RESET);
            for(int c = 0; c < bookshelfColumns; c++){
                coord = new Coordinates(r,c);
                if (tilesMap.containsKey(coord) )
                    colorTile = getColorFromTileType(tilesMap.get(coord));
                else
                    colorTile = BLACK;
                System.out.print(colorTile+"██ ");
            }
            System.out.print("\n");
        }
        System.out.println();
    }


    public void printLivingRoom(Map<Coordinates, Optional<ItemTile>> livingRoomMap) throws InvalidCoordinatesException {
        Coordinates coord;
        String colorTile;

        /* bookshelf settings */
        int livingRoomRows = 9;
        int livingRoomColumns = 9;
        System.out.println(WHITE_BOLD_BRIGHT+"== Here's the "+RED_BOLD+"Living room board"+WHITE_BOLD_BRIGHT+" =="+RESET);

        System.out.print("  ");
        for(int c = 0; c < livingRoomColumns; c++){
            System.out.print(WHITE_BOLD_BRIGHT+c+"  ");
        }
        System.out.println(RESET);

        for(int r = livingRoomRows - 1; r >= 0; r--){
            System.out.print(WHITE_BOLD_BRIGHT+r+" "+RESET);
            for(int c = 0; c < livingRoomColumns; c++){
                coord = new Coordinates(r,c);
                if (livingRoomMap.containsKey(coord) ){
                    if (livingRoomMap.get(coord).isPresent()){
                        colorTile = getColorFromTileType(livingRoomMap.get(coord).get());
                    } else {
                        colorTile = BLACK;
                    }
                    System.out.print(colorTile+"██ ");
                } else{
                    System.out.print("   ");
                }
            }
            System.out.print("\n");
        }
        System.out.println();
    }
    private String getColorFromTileType(ItemTile tile){
            switch (tile){
                case CAT -> {
                    return GREEN;
                }
                case BOOK -> {
                    return WHITE;
                }
                case GAME -> {
                    return YELLOW;
                }
                case FRAME -> {
                    return CYAN;
                }
                case TROPHY -> {
                    return BLUE;
                }
                case PLANT -> {
                    return PURPLE;
                }
        }

        return BLACK;
    }
}
