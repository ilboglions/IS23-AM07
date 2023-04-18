package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.lobby.Lobby;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class LobbyControllerTest {

    @Test
    @DisplayName("Test entering in lobby")
    void testEnter() throws RemoteException {
        LobbyController test = new LobbyController(new Lobby());

        assertTrue(test.enterInLobby("Test")); //Player added correctly
        assertFalse(test.enterInLobby("Test")); //Player with the same nickname already joined
    }

    @Test
    @DisplayName("Test game creation")
    void testCreation() throws RemoteException {
        LobbyController test = new LobbyController(new Lobby());

        //Invalid values, already tested in LobbyTest class
        assertTrue(test.createGame("Test", 0).isEmpty());
        assertTrue(test.createGame(null, 3).isEmpty());
        assertTrue(test.createGame("Test", 6).isEmpty());
        assertTrue(test.createGame("Test", -1).isEmpty());
        assertTrue(test.createGame("Test", 3).isEmpty());

        test.enterInLobby("Test");
        assertTrue(test.createGame("Test", 6).isEmpty());
        assertTrue(test.createGame("Test", -1).isEmpty());
        assertTrue(test.createGame("Test", 1).isEmpty());
        assertFalse(test.createGame("Test", 3).isEmpty()); //Created correctly, all the values are ok
    }

    @Test
    @DisplayName("Test adding player to game")
    void testAddPlayer() throws RemoteException {
        LobbyController test = new LobbyController(new Lobby());

        assertTrue(test.addPlayerToGame("Test").isEmpty()); //No player in lobby with this nickname
        assertTrue(test.addPlayerToGame(null).isEmpty());

        test.enterInLobby("Test");
        assertTrue(test.addPlayerToGame("Test").isEmpty()); //No game available

        test.createGame("Test", 3);
        assertTrue(test.addPlayerToGame("Test").isEmpty()); //No player in lobby available, and also we would have duplicate nickname

        test.enterInLobby("secondUser");
        assertTrue(test.addPlayerToGame("secondUser").isPresent()); //Correct adding to an available game

        test.enterInLobby("thirdUser");
        test.enterInLobby("fourthUser");
        assertTrue(test.addPlayerToGame("thirdUser").isPresent()); //Correct adding to available game and game started
        assertTrue(test.addPlayerToGame("fourthUser").isEmpty()); //No game available
    }

}