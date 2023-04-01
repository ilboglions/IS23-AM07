package it.polimi.ingsw.model.livingRoom;

import com.google.gson.Gson;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.EmptySlotException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.exceptions.SlotFullException;
import it.polimi.ingsw.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
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
        // the board must be refilled if the next player can only take single tiles => there are not 2 tiles adjacient in the board
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

    public boolean checkValidRetrieve(ArrayList<Coordinates> coo) throws EmptySlotException {
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
                else if(diffRow == 0 && diffCol != 0) {
                    // check parallel to x returns if the coordinates represent a segment parallel to x
                    // if it's not the case it returns false, otherwise true
                    // sort based on the x-axis
                    Collections.sort(coo, (curr, next) -> {
                        if( curr.getColumn() <= next.getColumn() )
                            return -1;
                        else
                            return 1;
                    });


                    return checkParallelX(coo);
                } else if(diffCol == 0 && diffRow != 0) {
                    // check parallel to y

                    // sort based on the y-axis
                    Collections.sort(coo, (curr, next) -> {
                        if( curr.getRow() <= next.getRow() )
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

    private boolean checkFreeSide(Coordinates coo) {
        int i = 2;
        int selRow[] = new int[i], selCol[] = new int[i];
        selRow[0] = coo.getRow() - 1;
        selRow[1] = coo.getRow() + 1;
        selCol[0] = coo.getColumn() - 1;
        selCol[1] = coo.getColumn() + 1;

        for(int x=0; x<i; x++) {
            // if the neighbour cell is in the range of the x-axis
            if(selRow[x] >= 0 && selRow[x] <= 8) {
                for(int y=0; y<i; y++) {
                    // if the neighbour cell is in the range of the y-axis
                    if(selCol[y] >= 0 && selCol[y] <= 8) {
                        // if the selected neighbour cell does not have a tile then the current tile has a free side
                        if(slot[selRow[x]][selCol[y]].getItemTile().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
