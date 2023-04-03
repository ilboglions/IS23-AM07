package it.polimi.ingsw.model.game;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

public class GameTest {

    @Test
    @DisplayName("Test the constructor")
    void testConstructor() {
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            Player testPlayer = new Player("Test");
            Game test = new Game(-1, testPlayer);
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            Player testPlayer = new Player("Test");
            Game test = new Game(100, testPlayer);
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            Player testPlayer = new Player("Test");
            Game test = new Game(0, testPlayer);
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            Player testPlayer = new Player("Test");
            Game test = new Game(1, testPlayer);
        });
        assertThrows(NullPointerException.class, ()->{
            Game test = new Game(3, null);
        });

        Player testPlayer = new Player("Test");
        assertDoesNotThrow(()-> new Game(3, testPlayer));
    }

    @Test
    @DisplayName("Test canStart method")
    void testCanStart() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertFalse(test.canStart());

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);

        assertTrue(test.canStart());
    }

    @Test
    @DisplayName("Test start method")
    void testStart() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NotAllPlayersHaveJoinedException.class, ()->{
           test.start();
        });

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);

        assertDoesNotThrow(()-> test.start());

        assertThrows(GameNotEndedException.class, ()->{
            test.start();
        });
    }

    @Test
    @DisplayName("Test updatePlayerPoints method")
    void testUpdatePoints() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(InvalidPlayerException.class, ()->{
           test.updatePlayerPoints("noUser");
        });

       assertDoesNotThrow(()-> test.updatePlayerPoints("Test"));
    }

    @Test
    @DisplayName("Test getPlayerInTurn method")
    void testGetPlayerInTurn() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException, GameNotStartedException, GameEndedException, NotEnoughSpaceException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(GameNotStartedException.class, ()->{
            test.getPlayerInTurn();
        });

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();

        assertDoesNotThrow(()->test.getPlayerInTurn());
        assertTrue(test.getPlayerInTurn().equals(testPlayer.getUsername()) || test.getPlayerInTurn().equals(secondPlayer.getUsername()) || test.getPlayerInTurn().equals(thirdPlayer.getUsername()));

        ArrayList<ItemTile> testTiles = new ArrayList<>();
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        for(int i=0; i<testPlayer.getBookshelf().getColumns(); i++) {
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
        }
        while(!test.getPlayerInTurn().equals(testPlayer.getUsername())) //Loop to go to the testPlayer turn to get the correct check on his bookshelf
            test.setPlayerTurn();

        test.checkBookshelfComplete();
        while(test.setPlayerTurn()); //Next turn until the game finishes
        assertThrows(GameEndedException.class, ()->{
           test.getPlayerInTurn();
        });
    }

    @Test
    @DisplayName("Test moveTiles method")
    void testMove() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException, InvalidCoordinatesException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(GameNotStartedException.class, ()->{
            test.moveTiles(new ArrayList<>(), 3);
        });

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();
        test.refillLivingRoom();

        assertThrows(InvalidCoordinatesException.class, ()->{
            test.moveTiles(new ArrayList<>(), 3);
        });
        assertThrows(NullPointerException.class, ()->{
            test.moveTiles(null, 3);
        });

        ArrayList<Coordinates> testList = new ArrayList<>();
        testList.add(new Coordinates(4,5));
        assertThrows(InvalidCoordinatesException.class, ()->{
            test.moveTiles(testList, -1);
        });
        assertThrows(InvalidCoordinatesException.class, ()->{
            test.moveTiles(testList, 100);
        });

        assertDoesNotThrow(()->test.moveTiles(testList, 0));

        assertThrows(EmptySlotException.class, ()->{
           test.moveTiles(testList, 1);
        });
    }

    @Test
    @DisplayName("Test getItemTile method")
    void testGetTile() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, InvalidCoordinatesException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
           test.getItemTiles(null);
        });

        ArrayList<Coordinates> testList = new ArrayList<>();
        testList.add(new Coordinates(3,4));
        assertThrows(EmptySlotException.class, ()->{
           test.getItemTiles(testList);
        });

        test.refillLivingRoom();
        assertDoesNotThrow(()->test.getItemTiles(testList));
    }

    @Test
    @DisplayName("Test checkRefill method")
    void testRefill() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException, InvalidCoordinatesException, GameNotStartedException, EmptySlotException, NotEnoughSpaceException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertTrue(test.checkRefill());

        test.refillLivingRoom();
        assertFalse(test.checkRefill());

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();

        ArrayList<Coordinates> testList = new ArrayList<>();
        testList.add(new Coordinates(3,4));
        test.moveTiles(testList, 2);
        assertFalse(test.checkRefill());
    }

    @Test
    @DisplayName("Test checkBookshelfComplete method")
    void testCheckComplete() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException, NotEnoughSpaceException, GameNotStartedException, GameEndedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertFalse(test.checkBookshelfComplete());

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();

        assertFalse(test.checkBookshelfComplete());

        ArrayList<ItemTile> testTiles = new ArrayList<>();
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        for(int i=0; i<testPlayer.getBookshelf().getColumns(); i++) {
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
        }
        while(!test.getPlayerInTurn().equals(testPlayer.getUsername())) //Loop to go to the testPlayer turn to get the correct check on his bookshelf
            test.setPlayerTurn();

        assertTrue(test.checkBookshelfComplete());
        assertTrue(test.checkBookshelfComplete()); //isLastTurn already set to 1
    }

    @Test
    @DisplayName("Test getWinner method")
    void testWinner() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException, NotEnoughSpaceException, GameNotStartedException, GameEndedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(GameNotStartedException.class, ()->{
           test.getWinner();
        });

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();

        assertThrows(GameNotEndedException.class, ()->{
            test.getWinner();
        });

        ArrayList<ItemTile> testTiles = new ArrayList<>();
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        testTiles.add(ItemTile.BOOK);
        for(int i=0; i<testPlayer.getBookshelf().getColumns(); i++) {
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
            testPlayer.getBookshelf().insertItemTile(i, testTiles);
        }
        while(!test.getPlayerInTurn().equals(testPlayer.getUsername()))
            test.setPlayerTurn();
        test.checkBookshelfComplete();

        while(test.setPlayerTurn()) { //Next turn until the game is finished
            //TODO: how to resolve that sometimes passes and other not (caused by the arraylist order of Players in game)
            assertThrows(GameEndedException.class, ()->{
                test.getWinner();
            });
        }
        assertEquals(testPlayer.getUsername(), test.getWinner());
    }

    @Test
    @DisplayName("Test addPlayer method")
    void testAddPlayer() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
           Game testNull = new Game(3, null);
        });
        assertThrows(NullPointerException.class, ()->{
            test.addPlayer(null);
        });

        Player duplicatePlayer = new Player("Test");
        assertThrows(NicknameAlreadyUsedException.class, ()->{
           test.addPlayer(duplicatePlayer);
        });

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        assertDoesNotThrow(()->test.addPlayer(secondPlayer));
        assertDoesNotThrow(()->test.addPlayer(thirdPlayer));

        Player fourthPlayer = new Player("fourthPlayer");
        assertThrows(PlayersNumberOutOfRange.class, ()->{
           test.addPlayer(fourthPlayer);
        });
    }

    @Test
    @DisplayName("Test searchPlayer method")
    void testSearch() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
           test.searchPlayer(null);
        });

        assertTrue(test.searchPlayer("Test").isPresent());
        assertFalse(test.searchPlayer("noPlayer").isPresent());
    }

    @Test
    @DisplayName("Test getIsStarted method")
    void testStarted() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, NotAllPlayersHaveJoinedException, GameNotEndedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertFalse(test.getIsStarted());

        Player secondPlayer = new Player("secondPlayer");
        Player thirdPlayer = new Player("thirdPlayer");
        test.addPlayer(secondPlayer);
        test.addPlayer(thirdPlayer);
        test.start();

        assertTrue(test.getIsStarted());
    }

    @Test
    @DisplayName("Test getPlayerMessage method")
    void testGetMessage() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, InvalidPlayerException, SenderEqualsRecipientException, NicknameAlreadyUsedException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
           test.getPlayerMessages(null);
        });
        assertThrows(InvalidPlayerException.class, ()->{
           test.getPlayerMessages("noPlayer");
        });

        assertEquals(0, test.getPlayerMessages("Test").size());

        test.postMessage("Test", Optional.empty(), "Test message");
        assertEquals(1, test.getPlayerMessages("Test").size());

        Player secondPlayer = new Player("secondPlayer");
        test.addPlayer(secondPlayer);
        test.postMessage("secondPlayer", Optional.of("Test"), "Private message");
        assertEquals(2, test.getPlayerMessages("Test").size());
    }

    @Test
    @DisplayName("Test postMessage method")
    void testPostMessage() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
            test.postMessage(null, Optional.empty(), "");
        });
        assertThrows(NullPointerException.class, ()->{
            test.postMessage("Test", Optional.empty(), null);
        });
        assertThrows(InvalidPlayerException.class, ()->{
            test.postMessage("noPlayer", Optional.empty(), "");
        });
        assertThrows(InvalidPlayerException.class, ()->{
            test.postMessage("Test", Optional.of("noPlayer"), "");
        });

        assertDoesNotThrow(()->test.postMessage("Test", Optional.empty(), "Test"));
    }

}
