package it.polimi.ingsw.model.livingRoom;

import com.google.gson.Gson;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.EmptySlotException;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.exceptions.SlotFullException;
import it.polimi.ingsw.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static it.polimi.ingsw.model.utilities.UtilityFunctions.MAX_PLAYERS;

/**
 * LivingRoomBoard represent the living room board and implements all the methods necessary
 */
public class LivingRoomBoard {


    private static final int NCELL_3PLAYER = 37;
    private static final int NCELL_4PLAYER = 45;
    private static final int NCELL_2PLAYER = 29;
    /**
     * matrix of slots to represent the board
     */
    private final Slot[][] slot;
    /**
     * board's number of rows
     */
    private final int rows = 9;
    /**
     * board's number of columns
     */
    private final int cols = 9;
    /**
     * board's number of valid cells; depends on the number of players that play the game
     */
    private final int numCells;


    /**
     * Constructor for the LivingRoomBoard class. It creates the layout of the board depending on how many players will play the game
     * @param numPlayers Number of players that play the game
     * @throws FileNotFoundException if the configuration file for the board is not found
     * @throws PlayersNumberOutOfRange if the given number of players is out of range
     */
    public LivingRoomBoard(int numPlayers) throws FileNotFoundException, PlayersNumberOutOfRange {

        slot = new Slot[rows][cols];
        int row,col;
        SlotType slotType;
        String confFilePath;
        Gson gson = new Gson();

        if(numPlayers <= 0 || numPlayers > MAX_PLAYERS) {
            throw new PlayersNumberOutOfRange("numPlayers is "+numPlayers+" it must be between 0 and "+MAX_PLAYERS+"!");
        } else if(numPlayers == 3) {
            this.numCells = NCELL_3PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/3PlayersPattern.json";
        } else if(numPlayers == MAX_PLAYERS) {
            this.numCells = NCELL_4PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/4orMorePlayersPattern.json";
        } else {
            this.numCells = NCELL_2PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/2PlayersPattern.json";
        }

        JsonLivingBoardCell[][] jsonCells = gson.fromJson(new FileReader(confFilePath), JsonLivingBoardCell[][].class);
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                row = jsonCells[i][j].row;
                col = jsonCells[i][j].col;
                slotType = jsonCells[i][j].slotType;
                slot[row][col] = new Slot(slotType, Optional.empty());
            }
        }
    }

    /**
     * Get a copy of the slot matrix
     * @return a copy of the slot matrix
     */
    protected Slot[][] getAllSlots(){
        Slot[][] newslot = new Slot[rows][cols];
        for(int i=0; i< rows; i++){
            for(int j=0; j < cols; j++){
                newslot[i][j] = new Slot(this.slot[i][j].getSlotType(),this.slot[i][j].getItemTile());
            }
        }
        return newslot;
    }

    /**
     * Custom fill the livingroomboard for testing purposes
     * @param tilesMap contains the coordinates and tiles to fill the board with.
     * @throws InvalidCoordinatesException
     * @throws SlotFullException
     */
    protected void customRefill(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException, SlotFullException {
       ArrayList<ItemTile> garbage = emptyBoard();
       for (Map.Entry<Coordinates, ItemTile> elem: tilesMap.entrySet()){
           addTile(elem.getKey(), elem.getValue());
        }

    }

    /**
     * Refill the living room board with new tiles
     * @return true if the board has to be refilled, false otherwise
     */
    public boolean checkRefill() {
        Slot currSlot;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols-1; j++) {
                currSlot = slot[i][j];
                if(currSlot.getSlotType() == SlotType.BASIC) {
                    if (currSlot.getItemTile().isPresent()) {
                        if (slot[i][j+1].getSlotType() == SlotType.BASIC && slot[i + 1][j].getItemTile().isPresent()) {
                            return false;
                        }
                    }
                }

                currSlot = slot[j][i];
                if(currSlot.getSlotType() == SlotType.BASIC) {
                    if (currSlot.getItemTile().isPresent()) {
                        if (slot[j+1][i].getSlotType() == SlotType.BASIC && slot[j+1][i].getItemTile().isPresent()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Empty the board
     * @return an array list containing the tiles that were on the board before the call of this method
     */
    public ArrayList<ItemTile> emptyBoard() {
        Slot curr;
        ArrayList<ItemTile> removed = new ArrayList<>();
        try {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    curr = slot[i][j];
                    if (curr.getItemTile().isPresent()) {
                        removed.add(curr.getItemTile().get());
                        this.removeTile(new Coordinates(i, j));
                    }
                }
            }
        } catch(InvalidCoordinatesException e){
            throw new RuntimeException(e);
        }
        return removed;
    }

    /**
     * Refill the living room board
     * @param tiles contains the tiles that are going to be inserted in the board
     * @throws NotEnoughTilesException is thrown if the given tiles are not enough
     */
    public void refillBoard(ArrayList<ItemTile> tiles) throws NotEnoughTilesException {
        Slot curr;
        if(tiles.size() < this.numCells)
            throw new NotEnoughTilesException("Not enough tiles");
        else {
            for(int i=0; i<this.rows; i++) {
                for(int j=0; j<this.cols; j++) {
                    curr = slot[i][j];
                    if(curr.getSlotType() == SlotType.BASIC) {
                        curr.setItemTile(Optional.of(tiles.get(0)));
                        tiles.remove(0);
                    }
                }
            }
        }
    }

    /**
     * Return the content of a slot from the living room board
     * @param coo is the coordinates object for the tile to be retrieved
     * @return Optional with the content of the slot requested
     * @throws InvalidCoordinatesException is thrown if the coordinates are out of range
     */
    public Optional<ItemTile> getTile(Coordinates coo) throws InvalidCoordinatesException {
    if(coo == null)
            throw new NullPointerException("Given coordinates are null");
        else {
            return slot[coo.getRow()][coo.getColumn()].getItemTile();
        }
    }

    /**
     * Used to add a single tile to the board
     * @param coo the coordinates of the slot where to put the new tile
     * @param itemTile the type of tile to be inserted
     * @throws SlotFullException is thrown if the requested slot has already a tile on it
     * @throws InvalidCoordinatesException is thrown if the coordinates are out of range
     */
    protected void addTile(Coordinates coo,ItemTile itemTile) throws SlotFullException, InvalidCoordinatesException {
        int row,col;
        Optional<ItemTile> newTile = Optional.of(itemTile);

        if (coo.getRow() >= this.rows || coo.getColumn() >= this.cols) throw new InvalidCoordinatesException("there is no cell in this coordinate!");

        if(coo == null) {
            throw new NullPointerException("Coordinates object is null");
        }
        row = coo.getRow();
        col = coo.getColumn();
        if(slot[row][col].getItemTile().isPresent()) {
            throw new SlotFullException("The slot "+row+" "+col+" has already a tile");
        } else {
            slot[row][col].setItemTile(newTile);
        }
    }

    /**
     * Used to remove a single tile from the board
     * @param coo coordinates of the requested slot
     */
    public void removeTile(Coordinates coo) {
        int row, col;

        if(coo == null)
            throw new NullPointerException("Coordinates object is null");
        else {
            row = coo.getRow();
            col = coo.getColumn();
            slot[row][col].setItemTile(Optional.empty());
        }
    }

    /**
     * Get the number of valid cells in this instance of the board
     * @return number of valid/usable cells in the board (numCells changes depending on the number of players)
     */
    public int getNumCells() {
        return numCells;
    }

    /**
     * Nested class used to cast the objects from the configuration file
     */
    protected class JsonLivingBoardCell {
        public int row,col;
        public SlotType slotType;

    }

    /**
     * Check if the given retrieve from the board is valid
     * @param coordinates the coordinates of the tiles that the caller wants to retrieve from the board
     * @return true if the requested move is valid, false otherwise
     * @throws EmptySlotException is thrown if a coordinates results to an empty slot
     */
    public boolean checkValidRetrieve(ArrayList<Coordinates> coordinates) throws EmptySlotException {
        ArrayList<Coordinates> coo = new ArrayList<Coordinates>(coordinates);
        int diffRow, diffCol;
        if(coo == null || coo.contains(null))
            throw new NullPointerException("Coordinates list is/contains null");
        else if(coo.isEmpty()) {
            return false;
        } else {
            for(Coordinates curr : coo) {
                if(slot[curr.getRow()][curr.getColumn()].getItemTile().isEmpty()) {
                    throw new EmptySlotException("Selected an empty slot");
                }
                if( !checkFreeSide(curr) ) {
                    return false;
                }
            }
            if(coo.size() == 1)
                return true;
            else {
                diffRow = Math.abs(coo.get(0).getRow() - coo.get(1).getRow());
                diffCol = Math.abs(coo.get(0).getColumn() - coo.get(1).getColumn());
                if( diffRow == 0 && diffCol == 0 )
                    return false;
                else if(diffRow == 0) {
                    coo.sort((curr, next) -> {
                        if (curr.getColumn() <= next.getColumn())
                            return -1;
                        else
                            return 1;
                    });
                    return checkParallelX(coo);
                } else if(diffCol == 0) {
                    coo.sort((curr, next) -> {
                        if (curr.getRow() <= next.getRow())
                            return -1;
                        else
                            return 1;
                    });
                    return checkParallelY(coo);
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Check if the series of coordinates creates a segment parallel to X
     * @param coo series of coordinates (MUST BE SORTED BY X FROM LEFT TO RIGHT)
     * @return true if the series of coordinates creates a segment parallel to X, false otherwise
     */
    private boolean checkParallelX(ArrayList<Coordinates> coo) {
        if(coo == null || coo.contains(null))
            throw new NullPointerException("Coordinates list is/contains null");

        for(int i=0; i<coo.size() - 1; i++) {
            if( coo.get(i).getRow() != coo.get(i+1).getRow() )
                return false;
            else {
                if(coo.get(i).getColumn() == coo.get(i+1).getColumn())
                    return false;
                if(coo.get(i).getColumn() != (coo.get(i+1).getColumn() - 1))
                    return false;
            }
        }
        return true;
    }
    /**
     * Check if the series of coordinates creates a segment parallel to Y
     * @param coo series of coordinates (MUST BE SORTED BY Y FROM TOP TO BOTTOM)
     * @return true if the series of coordinates creates a segment parallel to Y, false otherwise
     */
    private boolean checkParallelY(ArrayList<Coordinates> coo) {
        if(coo == null || coo.contains(null))
            throw new NullPointerException("Coordinates list is/contains null");

        for(int i=0; i<coo.size() - 1; i++) {
            if( coo.get(i).getColumn() != coo.get(i+1).getColumn() )
                return false;
            else {
                if(coo.get(i).getRow() == coo.get(i+1).getRow())
                    return false;
                if(coo.get(i).getRow() != (coo.get(i+1).getRow() - 1))
                    return false;
            }
        }
        return true;
    }

    /**
     * Check if a tile has a free side
     * @param coo coordinates of the requested slot
     * @return true if the tile has a free side, false otherwise
     * @throws EmptySlotException is thrown if the given coordinates result in an empty slot
     */
    private boolean checkFreeSide(Coordinates coo) throws EmptySlotException{
        int i = 2;
        int d;
        boolean a,b;

        if(slot[coo.getRow()][coo.getColumn()].getItemTile().isEmpty())
            throw new EmptySlotException("Checking for the free side of an empty slot");

        for( int x = 0; x < i; x++){

            a=false;
            b=false;
            d = x % 2 == 0 ? 1 : -1;
            try {
                a = this.getTile(new Coordinates(coo.getRow() + d, coo.getColumn())).isEmpty();
            }
            catch (InvalidCoordinatesException ignore){

            }
            try {
                b = this.getTile(new Coordinates(coo.getRow(), coo.getColumn() + d)).isEmpty();
            }
            catch (InvalidCoordinatesException ignore){

            }
            if ( a || b ) {
                return true;
            }
        }
        return false;

    }

}
