package it.polimi.ingsw.model.game;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Class used to test Game class
 */
public class GameTest {

    /**
     * This tests if the constructor is throwing correctly all the exceptions
     */
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

    /**
     * This tests if the method canStart is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     */
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

    /**
     * This tests if the method start is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     */
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

    /**
     * This tests if the method to update player points is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     */
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

    /**
     * This tests if the getter for the player turn is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     * @throws GameNotStartedException if the game has not started yet
     * @throws GameEndedException if the game is already ended
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     */
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

    /**
     * This tests if the method to move the tiles from the LivingRoomBoard to a PlayerBookshelf is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
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

    /**
     * This tests if the method to check if the tiles selected by the player could be chosen is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test getItemTile method")
    void testGetTile() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange, InvalidCoordinatesException {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
           test.checkValidRetrieve(null);
        });

        ArrayList<Coordinates> testList = new ArrayList<>();
        testList.add(new Coordinates(3,4));
        assertThrows(EmptySlotException.class, ()->{
           test.checkValidRetrieve(testList);
        });

        test.refillLivingRoom();
        assertDoesNotThrow(()->test.checkValidRetrieve(testList));
    }

    /**
     * This tests if the method to check if the LivingRoomBoard needs a refill is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     * @throws InvalidCoordinatesException if the coordinates are out of range
     * @throws GameNotStartedException if the game has not started yet
     * @throws EmptySlotException if the slot of the is empty
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     */
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

    /**
     * This tests if the method to move the tiles from the LivingRoomBoard to a PlayerBookshelf is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     * @throws GameNotStartedException if the game has not started yet
     * @throws GameEndedException if the game has already ended
     */
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

    /**
     * This tests if the method to get the winner of the game is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     * @throws GameNotStartedException if the game has not started yet
     * @throws GameEndedException if the game has already ended
     */
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

        while(test.setPlayerTurn() && !test.isLastPlayerTurn()) { //Next turn until the game is finished
            assertThrows(GameNotEndedException.class, ()->{
                test.getWinner();
            });
        }
        assertEquals(testPlayer.getUsername(), test.getWinner());
    }

    /**
     * This tests if the method to add a player to the game is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     */
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

    /**
     * This tests if the method search if a player has joined the game is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     */
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

    /**
     * This tests if the method to get if the game has started is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has not ended yet
     */
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

    /**
     * This tests if the method to get all the messages relative to a player is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     * @throws NicknameAlreadyUsedException if there is already a player with the same nickname
     * @throws InvalidPlayerException if the player is invalid
     * @throws SenderEqualsRecipientException if the sender and the recipient of the message are the equals
     */
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

        test.postMessage("Test", "Test message");
        assertEquals(1, test.getPlayerMessages("Test").size());

        Player secondPlayer = new Player("secondPlayer");
        test.addPlayer(secondPlayer);
        test.postMessage("secondPlayer", "Private message");
        assertEquals(2, test.getPlayerMessages("Test").size());
    }

    /**
     * This tests if the method to post a message in the chat is working correctly
     * @throws NegativeFieldException if the elements to draw are a negative number
     * @throws FileNotFoundException if the configuration file path is not correct
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the player number is less than 2 or grater than 4
     */
    @Test
    @DisplayName("Test postMessage method")
    void testPostMessage() throws NegativeFieldException, FileNotFoundException, NotEnoughCardsException, PlayersNumberOutOfRange {
        Player testPlayer = new Player("Test");
        Game test = new Game(3, testPlayer);

        assertThrows(NullPointerException.class, ()->{
            test.postMessage(null,  "");
        });
        assertThrows(NullPointerException.class, ()->{
            test.postMessage("Test", null);
        });
        assertThrows(InvalidPlayerException.class, ()->{
            test.postMessage("noPlayer",  "");
        });
        assertThrows(InvalidPlayerException.class, ()->{
            test.postMessage("Test","noPlayer", "");
        });

        assertDoesNotThrow(()->test.postMessage("Test", "Test"));
    }

}
