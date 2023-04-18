package it.polimi.ingsw.server.model.lobby;

import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.lobby.Lobby;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test Lobby class
 */
public class LobbyTest {
    /**
     * This tests if the exception are firing correctly for null values, player not present in the lobby or invalid values to create the game or player
     */
    @Test
    @DisplayName("Test all the exceptions")
    void checkException() throws NicknameAlreadyUsedException {
        Lobby test = new Lobby();

        assertThrows(NullPointerException.class, ()->{
           test.addPlayerToGame(null);
        });
        assertThrows(NullPointerException.class, ()->{
            test.createPlayer(null);
        });
        assertThrows(NullPointerException.class, ()->{
            test.createGame(4, null);
        });
        assertThrows(NoAvailableGameException.class, ()->{
            test.addPlayerToGame("testUser");
        });
        assertThrows(InvalidPlayerException.class, ()->{
            test.createGame(4, "testUser");
        });

        test.createPlayer("testUser");

        assertThrows(NicknameAlreadyUsedException.class, ()->{
            test.createPlayer("testUser");
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            test.createGame(100, "testUser");
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            test.createGame(-1, "testUser");
        });
        assertThrows(NicknameAlreadyUsedException.class, ()->{
            test.createGame(4, "testUser");
            test.createPlayer("testUser");
        });
        assertThrows(InvalidPlayerException.class, ()->{
           test.createGame(4, "testUser");
           test.addPlayerToGame("secondUser");
        });
    }

    /**
     * This tests if all the methods of the lobby are working correctly
     * @throws NicknameAlreadyUsedException if there is already a user with the same nickname
     * @throws InvalidPlayerException if the player is invalid
     * @throws BrokenInternalGameConfigurations if there is a problem with the file configuration
     * @throws NotEnoughCardsException if there are not enough cards to draw
     * @throws PlayersNumberOutOfRange if the number of player is less than 2 or grater than 4
     * @throws NoAvailableGameException if there isn't a game where the player can join
     */
    @Test
    @DisplayName("Test all the methods")
    void testMethods() throws NicknameAlreadyUsedException, InvalidPlayerException, BrokenInternalGameConfigurations, NotEnoughCardsException, PlayersNumberOutOfRange, NoAvailableGameException {
        Lobby test = new Lobby();
        test.createPlayer("testUser");
        test.createPlayer("secondUser");

        Game testGame = test.createGame(4, "testUser");
        assertEquals(testGame, test.addPlayerToGame("secondUser"));

        test.createPlayer("thirdUser");
        test.createPlayer("fourthUser");
        Game testSecondGame = test.createGame(2, "thirdUser");
        assertEquals(testGame, test.addPlayerToGame("fourthUser"));
    }
}
