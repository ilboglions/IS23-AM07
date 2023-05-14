package it.polimi.ingsw.server.model.livingRoom;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.SlotFullException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.*;

public class LivingRoomBoardTest {

    /**
     * This test verifies the correctness of the constructor method, exception cases
     * and Board filling
     * @throws FileNotFoundException
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("LivingRoomBoardTester")
    void ConstructorTest() throws FileNotFoundException, PlayersNumberOutOfRange, RemoteException {
        assertThrows(PlayersNumberOutOfRange.class, () ->{ new LivingRoomBoard(0);});
        assertThrows(PlayersNumberOutOfRange.class, () ->{ new LivingRoomBoard(5);});
        LivingRoomBoard testboard = new LivingRoomBoard(4);
        String confFilePath = "4orMorePlayersPattern.json";
        Gson gson = new Gson();
        LivingRoomBoard.JsonLivingBoardCell[][] jsonCells = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(confFilePath))), LivingRoomBoard.JsonLivingBoardCell[][].class);
        SlotType slotType;
        Slot[][] slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
        testboard = new LivingRoomBoard(3);
        confFilePath = "3PlayersPattern.json";
        gson = new Gson();
        jsonCells = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(confFilePath))), LivingRoomBoard.JsonLivingBoardCell[][].class);
        slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
        testboard = new LivingRoomBoard(2);
        confFilePath = "2PlayersPattern.json";
        gson = new Gson();
        jsonCells = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(confFilePath))), LivingRoomBoard.JsonLivingBoardCell[][].class);
        slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
    }

    /**
     * This test verifies the correctness of the checkRefill method.
     * @throws PlayersNumberOutOfRange
     * @throws InvalidCoordinatesException
     * @throws SlotFullException
     */
    @Test
    @DisplayName("CheckRefillTester")
    void checkRefillTest() throws PlayersNumberOutOfRange, InvalidCoordinatesException, SlotFullException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(2);
        ArrayList<ItemTile> removedtiles= board.emptyBoard();
        Map<Coordinates, ItemTile> inserttiles = new HashMap<>();
        inserttiles.put(new Coordinates(3,3), ItemTile.CAT);
        inserttiles.put(new Coordinates(5,3), ItemTile.TROPHY);
        inserttiles.put(new Coordinates(4,4), ItemTile.PLANT);
        inserttiles.put(new Coordinates(3,1), ItemTile.BOOK);
        board.customRefill(inserttiles);
        assertTrue(board.checkRefill());
        inserttiles.put(new Coordinates(4,3), ItemTile.CAT);
        board.customRefill(inserttiles);
        assertFalse(board.checkRefill());
    }


    /**
     * Testing the emptyBoard method
     * @throws FileNotFoundException
     * @throws PlayersNumberOutOfRange
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("EmptyBoardTester")
    void emptyBoardTest() throws PlayersNumberOutOfRange, InvalidCoordinatesException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(3);
        Slot[][] slotmatrix = board.getAllSlots();
        ArrayList<ItemTile> itemTileslist = new ArrayList<>();
        Optional<ItemTile> testtile;
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotmatrix[i][j].getItemTile().ifPresent(itemTileslist::add);
            }
        }
        ArrayList<ItemTile> removed = board.emptyBoard();
        assertTrue(itemTileslist.containsAll(removed));
        assertEquals(itemTileslist.size(), removed.size());
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                assertFalse(board.getTile(new Coordinates(i,j)).isPresent());
            }
        }
    }


    @Test
    @DisplayName("refillBoardTester")
    void refillBoardTest() throws PlayersNumberOutOfRange, NotEnoughTilesException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(4);
        ArrayList<ItemTile> tilesList = createList(board.getNumCells());
        board.refillBoard(new ArrayList<>(tilesList));
        Slot[][] slotmatrix = board.getAllSlots();
        for(int i=0; i<9; i++) {
            for (int j = 0; j < 9; j++) {
                if(slotmatrix[i][j].getSlotType() != SlotType.NOTCELL){
                    assertTrue(tilesList.contains(slotmatrix[i][j].getItemTile().get()));
                    tilesList.remove(slotmatrix[i][j].getItemTile().get());
                }
            }
        }
        assertEquals(0,tilesList.size());
        final ArrayList<ItemTile> tiles = createList(board.getNumCells()-1);
        assertThrows(NotEnoughTilesException.class, () -> board.refillBoard(new ArrayList<>(tiles)));
    }


    @Test
    @DisplayName("getTileTester")
    void getTileTest () throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(4);
        ArrayList<ItemTile> tilesList = createList(board.getNumCells());
        board.refillBoard(new ArrayList<>(tilesList));
        Slot[][] slotmatrix = board.getAllSlots();
        for(int i=0; i<9; i++) {
            for (int j = 0; j < 9; j++) {
                if(slotmatrix[i][j].getSlotType() != SlotType.NOTCELL){
                    assertEquals(board.getTile(new Coordinates(i,j)), slotmatrix[i][j].getItemTile());
                }
            }
        }
    }

    @Test
    @DisplayName("removeTileTester")
    void removeTile() throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(4);
        ArrayList<ItemTile> tilesList = createList(board.getNumCells());
        board.refillBoard(new ArrayList<>(tilesList));
        assertTrue(board.getTile(new Coordinates(3,3)).isPresent());
        board.removeTile(new Coordinates(3,3));
        assertFalse(board.getTile(new Coordinates(3,3)).isPresent());
    }

    @Test
    @DisplayName("checkValidRetrieveTester")
    void checkValidRetrieveTest() throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException, EmptySlotException, RemoteException {
        LivingRoomBoard board = new LivingRoomBoard(4);
        ArrayList<ItemTile> tilesList = createList(board.getNumCells());
        board.refillBoard(new ArrayList<>(tilesList));
        ArrayList<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(4,0));
        coordinates.add(new Coordinates(5,0));
        assertTrue(board.checkValidRetrieve(coordinates));
        board.removeTile(new Coordinates(4,0));
        board.removeTile(new Coordinates(5,0));
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(3,1));
        coordinates.add(new Coordinates(4,1));
        coordinates.add(new Coordinates(5,1));
        assertTrue(board.checkValidRetrieve(coordinates));
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(3,1));
        coordinates.add(new Coordinates(5,1));
        assertFalse(board.checkValidRetrieve(coordinates));
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(0,4));
        coordinates.add(new Coordinates(0,3));
        assertTrue(board.checkValidRetrieve(coordinates));
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(0,4));
        coordinates.add(new Coordinates(1,4));
        assertFalse(board.checkValidRetrieve(coordinates));
        coordinates = new ArrayList<>();
        assertFalse(board.checkValidRetrieve(coordinates));
        final ArrayList<Coordinates> finalcoord = new ArrayList<>();
        finalcoord.add(new Coordinates(4,0));
        assertThrows(EmptySlotException.class, ()->{board.checkValidRetrieve(finalcoord);});

    }




    /**
     * Method used to create a randomized list of itemtile
     * @param dim dimension of the list
     * @return a list of item tiles
     */
    private ArrayList<ItemTile> createList(int dim){
        int temp;
        ArrayList<ItemTile> list = new ArrayList<>();
        for(int i=0; i< dim; i++){
            temp = (int)(Math.random()*(5+1));
            if(temp == 0)
                list.add(ItemTile.CAT);
            else if (temp == 1)
                list.add(ItemTile.GAME);
            else if (temp == 2)
                list.add(ItemTile.FRAME);
            else if (temp == 3)
                list.add(ItemTile.PLANT);
            else if (temp == 4)
                list.add(ItemTile.BOOK);
            else
                list.add(ItemTile.TROPHY);
        }
        return list;
    }







}
