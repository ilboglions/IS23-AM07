package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.lobby.Lobby;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LobbyControllerTest {

    @Test
    @DisplayName("Test entering in lobby")
    void testEnter() throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException {
        LobbyController test = new LobbyController(new Lobby());

        assertEquals(null, test.enterInLobby("Test")); //Player added correctly
        assertThrows(NicknameAlreadyUsedException.class, () -> test.enterInLobby("Test")); //Player with the same nickname already joined
    }

    @Test
    @DisplayName("Test game creation")
    void testCreation() throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException {
        LobbyController test = new LobbyController(new Lobby());

        //Invalid values, already tested in LobbyTest class
        assertThrows( InvalidPlayerException.class, () -> test.createGame("Test", 0));
        assertThrows( InvalidPlayerException.class, () -> test.createGame("Test", 6));
        assertThrows( InvalidPlayerException.class, () -> test.createGame("Test", -1));
        assertThrows( InvalidPlayerException.class, () -> test.createGame("Test", 3));

        test.enterInLobby("Test");
        assertThrows( PlayersNumberOutOfRange.class, () -> test.createGame("Test", 0));
        assertThrows( PlayersNumberOutOfRange.class, () -> test.createGame("Test", 6));
        assertThrows( PlayersNumberOutOfRange.class, () -> test.createGame("Test", -1));
    }

    @Test
    @DisplayName("Test adding player to game")
    void testAddPlayer() throws RemoteException, NicknameAlreadyUsedException, NoAvailableGameException , PlayersNumberOutOfRange, InvalidPlayerException, BrokenInternalGameConfigurations, NotEnoughCardsException {
        LobbyController test = new LobbyController(new Lobby());
        assertThrows(InvalidPlayerException.class ,() -> test.addPlayerToGame("Test")); //No player in lobby with this nickname


        test.enterInLobby("Test");
        assertThrows(NoAvailableGameException.class, () -> test.addPlayerToGame("Test"));


        test.createGame("Test", 3);
        assertThrows(InvalidPlayerException.class, ()-> test.addPlayerToGame("Test")); //No player in lobby available, and also we would have duplicate nickname

        test.enterInLobby("secondUser");
        assertEquals(test.addPlayerToGame("secondUser").getClass(), GameController.class); //Correct adding to an available game

        test.enterInLobby("thirdUser");
        test.enterInLobby("fourthUser");
        assertEquals(test.addPlayerToGame("thirdUser").getClass(), GameController.class); //Correct adding to available game and game started
        assertThrows(NoAvailableGameException.class, () -> test.addPlayerToGame("fourthUser")); //No game available
    }

}