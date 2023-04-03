package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.exceptions.NicknameAlreadyUsedException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LobbyTest {
    Lobby lobbyTest = new Lobby();
    @Before
    public void createLobby() throws NicknameAlreadyUsedException {

        lobbyTest.createPlayer("FunnyPlayer");
    }


    @Test
    @DisplayName("Check exceptions")
    void checkException() throws NicknameAlreadyUsedException {
        createLobby();
        assertThrows(NicknameAlreadyUsedException.class, () -> lobbyTest.createPlayer("FunnyPlayer"));
        assertThrows(PlayersNumberOutOfRange.class, () -> lobbyTest.createGame(5,"FunnyPlayer"));
        assertThrows(InvalidPlayerException.class, () -> lobbyTest.createGame(2,"FunyPlayer"));
    }
    @Test
    @DisplayName("Check createPlayer method")
    void createPlayerTest() {
        assertThrows(NicknameAlreadyUsedException.class, () ->  lobbyTest.createPlayer("FunnyPlayer"));
    }
}
