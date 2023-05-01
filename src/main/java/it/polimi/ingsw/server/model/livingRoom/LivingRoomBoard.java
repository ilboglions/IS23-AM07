package it.polimi.ingsw.server.model.livingRoom;

import com.google.gson.Gson;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.SlotFullException;
import it.polimi.ingsw.server.model.listeners.BoardListener;
import it.polimi.ingsw.server.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.MAX_PLAYERS;

public class LivingRoomBoard {


    private static final int NCELL_3PLAYER = 37;
    private static final int NCELL_4PLAYER = 45;
    private static final int NCELL_2PLAYER = 29;

    private final Slot[][] slot;
    private final int rows = 9;
    private final int cols = 9;
    private final int numCells;

    private final BoardListener boardListener;

    public void subscribeToListener(BoardSubscriber subscriber){
        boardListener.addSubscriber(subscriber);
    }

    /*public LivingRoomBoard() throws FileNotFoundException {
        //create the pattern of the living room board
        //let's create the pattern per rows
        slot = new Slot[rows][cols];
        int row,col;
        SlotType slotType;
        //read a json file with the configuration of the board, from the file you will have coordinates and type
        Gson gson = new Gson();

        JsonLivingBoardCell[][] jsonCells = gson.fromJson(new FileReader("src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/livingRoomBoardPattern.json"), JsonLivingBoardCell[][].class);
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                row = jsonCells[i][j].row;
                col = jsonCells[i][j].col;
                slotType = jsonCells[i][j].slotType;
                slot[row][col] = new Slot(slotType, Optional.empty());
            }
        }
    }
    */
    /*
        This overload creates a different pattern of livingRoom based on the number of player.
        In the patterns there is no distinction of ATLEAST4, ATLEAST3 or BASIC cell; there will be only BASIC and NOTCELL
        slots.
    */

    public LivingRoomBoard(int numPlayers) throws FileNotFoundException, PlayersNumberOutOfRange {
        //create the pattern of the living room board
        //let's create the pattern per rows
        slot = new Slot[rows][cols];
        this.boardListener = new BoardListener();

        int row,col;
        SlotType slotType;
        String confFilePath;
        //read a json file with the configuration of the board, from the file you will have coordinates and type
        Gson gson = new Gson();

        if(numPlayers <= 0 || numPlayers > MAX_PLAYERS) {
            throw new PlayersNumberOutOfRange("numPlayers is "+numPlayers+" it must be between 0 and "+MAX_PLAYERS+"!");
        } else if(numPlayers == 3) {
            this.numCells = NCELL_3PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/server/model/livingRoom/confFiles/3PlayersPattern.json";
        } else if(numPlayers == MAX_PLAYERS) {
            this.numCells = NCELL_4PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/server/model/livingRoom/confFiles/4orMorePlayersPattern.json";
        } else {
            this.numCells = NCELL_2PLAYER;
            confFilePath = "src/main/java/it/polimi/ingsw/server/model/livingRoom/confFiles/2PlayersPattern.json";
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



        boardListener.onBoardChange(this.getItemTilesMapFromBoard());

    }

    /**
     * Private method used to gets an itemTiles map for listeners
     * @return the map containing the coordinates and an optional of the item tile
     */
    private Map<Coordinates,Optional<ItemTile>> getItemTilesMapFromBoard()  {

        Map<Coordinates,Optional<ItemTile>> itemTilesmap = new HashMap<>();
        for(int i=0; i< this.rows; i++){
            for(int j=0; j < this.cols; j++){
              if(slot[i][j].getSlotType().equals(SlotType.BASIC)){
                  try {
                      itemTilesmap.put(new Coordinates(i,j), slot[i][j].getItemTile());
                  } catch (InvalidCoordinatesException e) {
                      throw new RuntimeException(e);
                  }
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

    public boolean checkRefill() {
        // this method tells you if the livingRoom needs to be refilled with new tiles or not
        // the board must be refilled if the next player can only take single tiles => there are not 2 tiles adjacient in the board
        Slot currSlot;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols-1; j++) {
                currSlot = slot[i][j];
                if(currSlot.getSlotType() == SlotType.BASIC) { // if the slot is a valid one
                    if (currSlot.getItemTile().isPresent()) { // and there is a tile on it
                        if (slot[i][j+1].getSlotType() == SlotType.BASIC && slot[i + 1][j].getItemTile().isPresent()) {
                            return false;
                        }
                    }
                }

                currSlot = slot[j][i];
                if(currSlot.getSlotType() == SlotType.BASIC) { // if the slot is a valid one
                    if (currSlot.getItemTile().isPresent()) { // and there is a tile on it
                        if (slot[j+1][i].getSlotType() == SlotType.BASIC && slot[j+1][i].getItemTile().isPresent()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<ItemTile> emptyBoard() {
        // this method will empty the board returning a list with the tiles removed
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

    public void refillBoard(ArrayList<ItemTile> tiles) throws NotEnoughTilesException {
        Slot curr;
        if(tiles.size() < this.numCells)
            throw new NotEnoughTilesException("Not enough tiles");
        else {
            for(int i=0; i<this.rows; i++) {
                for(int j=0; j<this.cols; j++) {
                    curr = slot[i][j];
                    if(curr.getSlotType() == SlotType.BASIC) {
                        curr.setItemTile(Optional.of(tiles.get(0))); // create an optional with the tile to be inserted and put the optional inside the slot
                        tiles.remove(0);
                    }
                }
            }
        }
    }

    public Optional<ItemTile> getTile(Coordinates coo) throws InvalidCoordinatesException {
    if(coo == null)
            throw new NullPointerException("Given coordinates are null");
        else {
            return slot[coo.getRow()][coo.getColumn()].getItemTile();
        }
    }
    public void addTile(Coordinates coo,ItemTile itemTile) throws SlotFullException, InvalidCoordinatesException {
        int row,col;
        Optional<ItemTile> newTile = Optional.of(itemTile);

        if (coo.getRow() >= this.rows || coo.getColumn() >= this.cols) throw new InvalidCoordinatesException("there is no cell in this coordinate!");

        if(coo == null) {
            throw new NullPointerException("Coordinates object is null");
        }
        row = coo.getRow();
        col = coo.getColumn();
        if(slot[row][col].getItemTile().isPresent()) {
            /*
                it is not possible to put a new tile in a slot which is full at the moment.
                if you want to replace the tile, remove it first and then add the new tile
             */
            throw new SlotFullException("The slot "+row+" "+col+" has already a tile");
        } else {
            // add a new tile, the spot is empty
            slot[row][col].setItemTile(newTile);
        }
    }

    public void removeTile(Coordinates coo) {
        int row, col;

        if(coo == null)
            throw new NullPointerException("Coordinates object is null");
        else {
            row = coo.getRow();
            col = coo.getColumn();
            // if the slot is a NOTCELL it does not matter, further checks are useless
            slot[row][col].setItemTile(Optional.empty());
        }
    }

    public int getNumCells() {
        return numCells;
    }

    protected class JsonLivingBoardCell {
        public int row,col;
        public SlotType slotType;

    }

    public boolean checkValidRetrieve(ArrayList<Coordinates> coordinates) throws EmptySlotException {
        ArrayList<Coordinates> coo = new ArrayList<>(coordinates);
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
                    // the tile does not have a free side, it cannot be retrieved and the move is not valid
                    return false;
                }
            }
            // if the list contains one coordinate, then the move is valid only if it has a free side, which we previously verified
            if(coo.size() == 1)
                return true;
            else {
                // if the list is not empty, and it does not contain just one element then the list size is at least two and we need to
                // verify if the segment is purely horizontal or vertical
                diffRow = Math.abs(coo.get(0).getRow() - coo.get(1).getRow());
                diffCol = Math.abs(coo.get(0).getColumn() - coo.get(1).getColumn());
                if( diffRow == 0 && diffCol == 0 )
                    return false;
                else if(diffRow == 0) {
                    // check parallel to x returns if the coordinates represent a segment parallel to x
                    // if it's not the case it returns false, otherwise true
                    // sort based on the x-axis
                    coo.sort((curr, next) -> {
                        if (curr.getColumn() <= next.getColumn())
                            return -1;
                        else
                            return 1;
                    });


                    return checkParallelX(coo);
                } else if(diffCol == 0) {
                    // check parallel to y

                    // sort based on the y-axis
                    coo.sort((curr, next) -> {
                        if (curr.getRow() <= next.getRow())
                            return -1;
                        else
                            return 1;
                    });

                    return checkParallelY(coo);
                } else {
                    // this else is equal to the following: else if(diffRow != 0 && diffCol != 0)
                    return false;
                }
            }
        }
    }

    // in order to use properly this method the coordinates list needs to be ordered from top to bottom and from left to right
    // example: 00 -> 01 -> 03 is ordered properly and the check will return false
    // example: 00 -> 03 -> 01 is not ordered properly
    // it is the same for vertical alignments, start from top to bottom
    private boolean checkParallelX(ArrayList<Coordinates> coo) {
        if(coo == null || coo.contains(null))
            throw new NullPointerException("Coordinates list is/contains null");

        for(int i=0; i<coo.size() - 1; i++) {
            if( coo.get(i).getRow() != coo.get(i+1).getRow() )
                return false;
            else {
                // cannot have same coordinate two times
                if(coo.get(i).getColumn() == coo.get(i+1).getColumn())
                    return false;
                if(coo.get(i).getColumn() != (coo.get(i+1).getColumn() - 1))
                    return false;
            }
        }
        return true;
    }
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

    private boolean checkFreeSide(Coordinates coo)  {
        int i = 2;
        int d;

        for( int x = 0; x < i; x++){

                d = x % 2 == 0 ? 1 : -1;
                try {
                    if (this.getTile(new Coordinates(coo.getRow() + d, coo.getColumn())).isEmpty() || this.getTile(new Coordinates(coo.getRow(), coo.getColumn() + d)).isEmpty())
                        return true;
                }catch (InvalidCoordinatesException ignore){

                }
        }
        return false;

    }

    public void triggerListener(String userToBeUpdated){
        Objects.requireNonNull(userToBeUpdated);

        this.boardListener.triggerListener(userToBeUpdated, this.getItemTilesMapFromBoard());
    }

}
