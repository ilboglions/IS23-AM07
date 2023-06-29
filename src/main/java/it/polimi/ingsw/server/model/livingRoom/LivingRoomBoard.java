package it.polimi.ingsw.server.model.livingRoom;

import com.google.gson.Gson;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.SlotFullException;
import it.polimi.ingsw.server.model.listeners.BoardListener;
import it.polimi.ingsw.server.model.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.MAX_PLAYERS;

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

    private final BoardListener boardListener;

    /**
     * This method subscribe a subscriber to the listener of the LivingRoomBoard
     * @param subscriber the subscriber that needs to be subscribed to the listener
     */
    public void subscribeToListener(BoardSubscriber subscriber){
        boardListener.addSubscriber(subscriber);
    }


    /**
     * Constructor for the LivingRoomBoard class. It creates the layout of the board depending on how many players will play the game
     * @param numPlayers Number of players that play the game
     * @throws PlayersNumberOutOfRange if the given number of players is out of range
     */
    public LivingRoomBoard(int numPlayers) throws PlayersNumberOutOfRange {

        slot = new Slot[rows][cols];
        this.boardListener = new BoardListener();

        int row,col;
        SlotType slotType;
        InputStream confFilePath;
        Gson gson = new Gson();

        if(numPlayers <= 0 || numPlayers > MAX_PLAYERS) {
            throw new PlayersNumberOutOfRange("numPlayers is "+numPlayers+" it must be between 0 and "+MAX_PLAYERS+"!");
        } else if(numPlayers == 3) {
            this.numCells = NCELL_3PLAYER;
            confFilePath = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("3PlayersPattern.json"));
        } else if(numPlayers == MAX_PLAYERS) {
            this.numCells = NCELL_4PLAYER;
            confFilePath = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("4orMorePlayersPattern.json"));
        } else {
            this.numCells = NCELL_2PLAYER;
            confFilePath = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("2PlayersPattern.json"));
        }

        JsonLivingBoardCell[][] jsonCells = gson.fromJson(new InputStreamReader(confFilePath), JsonLivingBoardCell[][].class);
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                row = jsonCells[i][j].row;
                col = jsonCells[i][j].col;
                slotType = jsonCells[i][j].slotType;
                slot[row][col] = new Slot(slotType, Optional.empty());
            }
        }



        boardListener.onBoardChange(this.getItemTilesMapFromBoard());

    }

    /**
     * Get a copy of the slot matrix
     * @return a copy of the slot matrix
     */
    private Map<Coordinates,ItemTile> getItemTilesMapFromBoard()  {

        Map<Coordinates,ItemTile> itemTilesmap = new HashMap<>();
        Optional<ItemTile> tmpTile;
        for(int i=0; i< this.rows; i++){
            for(int j=0; j < this.cols; j++){
              if(slot[i][j].getSlotType().equals(SlotType.BASIC)){
                  try {
                      tmpTile = slot[i][j].getItemTile();
                      if(tmpTile.isPresent())
                          itemTilesmap.put(new Coordinates(i,j), tmpTile.get());
                      else
                          itemTilesmap.put(new Coordinates(i,j), ItemTile.EMPTY);
                  } catch (InvalidCoordinatesException ignored) {} //this exception never occurs since the coordinates are always valid
              }
            }
        }
        return itemTilesmap;
    }

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
     * @throws InvalidCoordinatesException if one of the coordinates of the map is invalid
     * @throws SlotFullException if one of the slots has already a tile in it
     */
    protected void customRefill(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException, SlotFullException {
       emptyBoard();
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
     * This method removes all the tiles from the Board
     * @return a list of all the tiles removed
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
        } catch(InvalidCoordinatesException ignored){}
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
            boardListener.onBoardChange(this.getItemTilesMapFromBoard());
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

        if(coo == null) {
            throw new NullPointerException("Coordinates object is null");
        }

        if (coo.getRow() >= this.rows || coo.getColumn() >= this.cols) throw new InvalidCoordinatesException("there is no cell in this coordinate!");

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
            boardListener.onBoardChange(this.getItemTilesMapFromBoard());
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
     * Unsubscribes a player from the boardListener
     * @param username username of the player to be removed
     */
    public void unsubscribeFromListener(String username) {
        boardListener.removeSubscriber(username);
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
     * @param coords the coordinates of the tiles that the caller wants to retrieve from the board
     * @return true if the requested move is valid, false otherwise
     * @throws EmptySlotException is thrown if a coordinates results to an empty slot
     */
    public boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException{
        boolean result;
        switch (coords.size()) {
            case 1 -> result = checkFreeSide(coords.get(0));
            case 2 -> result = checkFreeSide(coords.get(0)) && checkFreeSide(coords.get(1)) && checkNeighbors(coords.get(0), coords.get(1)) != -1;
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

    /**
     * Trigger the subscriber related the boardListener of a specified player
     * @param userToBeUpdated username of the player to be updated
     */
    public void triggerListener(String userToBeUpdated){
        Objects.requireNonNull(userToBeUpdated);

        this.boardListener.triggerListener(userToBeUpdated, this.getItemTilesMapFromBoard());
    }

    protected BoardListener getBoardListener() {
        return boardListener;
    }
}
