package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {
    @Test
    @DisplayName("Test all the exceptions")
    void checkException() throws NicknameAlreadyUsedException {
        Lobby test = new Lobby();

        assertThrows(NullPointerException.class, ()->{
           test.addPlayerToGame(null);
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
