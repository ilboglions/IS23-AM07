package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.lobby.Lobby;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LobbyControllerTest {

    @Test
    @DisplayName("Test entering in lobby")
    void testEnter() throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException {
        LobbyController test = new LobbyController(new Lobby());
        test.getSubscriberUsername();

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

    @Test
    @DisplayName("initializeTimer, handleCrashedPlayer")
    void testInitializeTimer() throws RemoteException, InvalidPlayerException, PlayersNumberOutOfRange, InterruptedException, NicknameAlreadyUsedException, BrokenInternalGameConfigurations, NoAvailableGameException, PlayerNotFoundException {
        Lobby lobby = new Lobby();
        LobbyController controller = new LobbyController(lobby);

        controller.enterInLobby("host");
        controller.triggerHeartBeat("host");
        assertTrue(controller.getTimers().containsKey("host") && controller.getTimers().keySet().size() == 1);
        assertTrue(controller.getTimers().get("host").isScheduled());
        controller.triggerHeartBeat("host");
        Thread.sleep(16000);

        assertThrows(PlayerNotFoundException.class, () -> {
            controller.handleCrashedPlayer("host");
        });

        controller.enterInLobby("host");
        controller.enterInLobby("test2");
        controller.createGame("host", 2);
        controller.addPlayerToGame("test2");
        Map<GameModelInterface, GameController> map = controller.getGameControllers();
        for(Map.Entry<GameModelInterface, GameController> entry : map.entrySet()){
            entry.getValue().handleCrashedPlayer("host");
        }

        assertDoesNotThrow(()->{
            controller.enterInLobby("host");
        });
    }

    @Test
    @DisplayName("notifyChangedGameStatus")
    void testNotifyChangedGameStatus() throws RemoteException, NicknameAlreadyUsedException, InvalidPlayerException, PlayersNumberOutOfRange, NoAvailableGameException, InterruptedException {
        Lobby lobby = new Lobby();
        LobbyController controller = new LobbyController(lobby);

        controller.enterInLobby("host");
        controller.enterInLobby("x");
        RemoteGameController game = controller.createGame("host",2);
        controller.addPlayerToGame("x");
        game.triggerAllListeners("host");

        Map<GameModelInterface, GameController> map = controller.getGameControllers();
        for(Map.Entry<GameModelInterface, GameController> entry : map.entrySet()){
            controller.notifyChangedGameStatus(GameState.ENDED, entry.getKey());
        }
    }

}