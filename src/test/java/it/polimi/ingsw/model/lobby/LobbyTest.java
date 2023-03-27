package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LobbyTest {
    @Test
    @DisplayName("Check exceptions")
    void checkException() {
        Lobby lobbyTest = new Lobby();
        assertThrows(NegativeFieldException.class, () -> lobbyTest.createPlayer("FunnyPlayer"));

    }
}
