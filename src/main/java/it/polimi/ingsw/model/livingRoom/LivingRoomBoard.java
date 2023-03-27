package it.polimi.ingsw.model.livingRoom;

import com.google.gson.Gson;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.exceptions.SlotFullException;
import it.polimi.ingsw.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Optional;

public class LivingRoomBoard {

    private final Slot[][] slot;
    private final int rows = 9;
    private final int cols = 9;
    private final int numCells;

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
        int row,col;
        SlotType slotType;
        String confFilePath;
        //read a json file with the configuration of the board, from the file you will have coordinates and type
        Gson gson = new Gson();

        if(numPlayers <= 0) {
            throw new PlayersNumberOutOfRange("numPlayers is negative !!");
        } else if(numPlayers == 3) {
            this.numCells = 37;
            confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/3PlayersPattern.json";
        } else if(numPlayers >= 4) {
            this.numCells = 45;
            confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/4orMorePlayersPattern.json";
        } else {
            this.numCells = 29;
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

    public boolean checkRefill() {
        // this method tells you if the livingRoom needs to be refilled with new tiles or not
        boolean result;
        Slot currSlot;
        Optional<ItemTile> temp;
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
        ArrayList<ItemTile> removed = new ArrayList<ItemTile>();
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                curr = slot[i][j];
                if(curr.getItemTile().isPresent()) {
                    removed.add(curr.getItemTile().get());
                    this.removeTile(new Coordinates(i,j));
                }
            }
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

    public Optional<ItemTile> getTile(Coordinates coo) {
        if(coo == null)
            throw new NullPointerException("Given coordinates are null");
        else {
            return slot[coo.getRow()][coo.getColumn()].getItemTile();
        }
    }
    public void addTile(Coordinates coo,ItemTile itemTile) throws SlotFullException {
        int row,col;
        Optional<ItemTile> newTile = Optional.of(itemTile);
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

    private class JsonLivingBoardCell {
        public int row,col;
        public SlotType slotType;

    }
}
