package it.polimi.ingsw.model.livingRoom;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Optional;

public class LivingRoomBoard {

    private Slot[][] slot;
    private final static int rows = 9;
    private final static int cols = 9;
    public LivingRoomBoard() throws FileNotFoundException {
        //create the pattern of the living room board
        //let's create the pattern per rows
        slot = new Slot[rows][cols];
        int row,col;
        SlotType slotType;
        //read a json file with the configuration of the board, from the file you will have coordinates and type
        Gson gson = new Gson();

        JsonLivingBoardCell[][] jsonCells = gson.fromJson(new FileReader("livingRoom/livingRoomBoardPattern.json"), JsonLivingBoardCell[][].class);
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                row = jsonCells[i][j].row;
                col = jsonCells[i][j].col;
                slotType = jsonCells[i][j].slotType;
                slot[row][col] = new Slot(slotType, Optional.empty());
            }
        }
    }

    public Optional<ItemTile> getTile(Coordinates coo) {
        if(coo == null)
            throw new NullPointerException("Given coordinates are null");
        else {
            return slot[coo.getY()][coo.getX()].getItemTile();
        }
    }
    public boolean addTile(Coordinates coo,ItemTile itemTile) {
        int x,y; // x -> col , y -> row
        Optional<ItemTile> newTile = Optional.of(itemTile);
        x = coo.getX();
        y = coo.getY();
        if(slot[y][x].getItemTile().isPresent()) {  // if there is already a tile in this slot then u can't add a new tile
            return false;
        } else { // add a new tile, the spot is empty
            slot[y][x].setItemTile(newTile);
            return true;
        }
    }

    private class JsonLivingBoardCell {
        public int row,col;
        public SlotType slotType;

    }
}
