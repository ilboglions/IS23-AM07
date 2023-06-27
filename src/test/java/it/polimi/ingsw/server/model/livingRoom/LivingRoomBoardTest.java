package it.polimi.ingsw.server.model.livingRoom;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
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
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("LivingRoomBoardTester")
    void ConstructorTest() throws FileNotFoundException, PlayersNumberOutOfRange {
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
    void checkRefillTest() throws PlayersNumberOutOfRange, InvalidCoordinatesException, SlotFullException {
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
        removedtiles= board.emptyBoard();
        inserttiles.clear();
        inserttiles.put(new Coordinates(4,3), ItemTile.TROPHY);
        inserttiles.put(new Coordinates(5,3), ItemTile.CAT);
        inserttiles.put(new Coordinates(6,3), ItemTile.TROPHY);
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
    void emptyBoardTest() throws PlayersNumberOutOfRange, InvalidCoordinatesException {
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
    void refillBoardTest() throws PlayersNumberOutOfRange, NotEnoughTilesException {
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
    void getTileTest () throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException {
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
    void removeTile() throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException {
        LivingRoomBoard board = new LivingRoomBoard(4);
        ArrayList<ItemTile> tilesList = createList(board.getNumCells());
        board.refillBoard(new ArrayList<>(tilesList));
        assertTrue(board.getTile(new Coordinates(3,3)).isPresent());
        board.removeTile(new Coordinates(3,3));
        assertFalse(board.getTile(new Coordinates(3,3)).isPresent());
    }

    @Test
    @DisplayName("checkValidRetrieveTester")
    void checkValidRetrieveTest() throws PlayersNumberOutOfRange, NotEnoughTilesException, InvalidCoordinatesException, EmptySlotException {
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

        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(4,3));
        coordinates.add(new Coordinates(3,3));
        coordinates.add(new Coordinates(4,4));
        assertFalse(board.checkValidRetrieve(coordinates));

        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(4,4));
        coordinates.add(new Coordinates(4,3));
        coordinates.add(new Coordinates(4,5));
        assertFalse(board.checkValidRetrieve(coordinates));

    }

    @Test
    @DisplayName("Test the exceptions")
    void testException() throws PlayersNumberOutOfRange, InvalidCoordinatesException, SlotFullException {
        LivingRoomBoard board = new LivingRoomBoard(4);

        assertThrows(NullPointerException.class, ()->{
            board.addTile(null, ItemTile.TROPHY);
        });
        assertThrows(NullPointerException.class, ()->{
            board.getTile(null);
        });
        assertThrows(NullPointerException.class, ()->{
            board.removeTile(null);
        });

        board.addTile(new Coordinates(3,4), ItemTile.TROPHY);
        assertThrows(SlotFullException.class, ()->{
            board.addTile(new Coordinates(3,4), ItemTile.CAT);
        });

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

    @Test
    @DisplayName("GameController subscribeToListener LivingRoomBoard")
    void testSubscribeToListener() throws RemoteException, NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, PlayersNumberOutOfRange {
        LivingRoomBoard board = new LivingRoomBoard(2);
        SubscriberForTest sub = new SubscriberForTest();

        board.subscribeToListener((BoardSubscriber) sub);
        assertTrue(board.getBoardListener().getSubscribers().contains(sub) && board.getBoardListener().getSubscribers().size() == 1);
    }

    private class SubscriberForTest implements BoardSubscriber {

        @Override
        public String getSubscriberUsername() throws RemoteException {
            return null;
        }

        @Override
        public void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) throws RemoteException {

        }
    }





}
