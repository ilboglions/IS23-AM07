package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Chat;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameControllerTest {

    @Test
    @DisplayName("checkValidRetrieve")
    void testCheckValidRetrieve() throws NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, PlayersNumberOutOfRange, RemoteException, NicknameAlreadyUsedException, GameNotStartedException, GameEndedException, InvalidCoordinatesException, NotAllPlayersHaveJoinedException, GameNotEndedException, EmptySlotException, PlayerNotInTurnException {

        Player X,Y;
        X = new Player("X");
        Y = new Player("Y");
        Game gameModel = new Game(2,X);
        gameModel.addPlayer(Y);
        GameController gameController = new GameController(gameModel);

        ArrayList<Coordinates> toCheckTrue = new ArrayList<>();
        ArrayList<Coordinates> toCheckFalse = new ArrayList<>();

        toCheckTrue.add(new Coordinates(4,1));
        toCheckFalse.add(new Coordinates(4,4));

        assertThrows( GameNotStartedException.class, () -> gameController.checkValidRetrieve("X", toCheckTrue));

        gameModel.start();
        // set playerInTurn to X
        while(!gameModel.getPlayerInTurn().equals("X")) {
            gameModel.setPlayerTurn();
        }


        assertThrows( PlayerNotInTurnException.class, () -> gameController.checkValidRetrieve("Y",toCheckTrue));

        assertTrue(gameController.getSelectedTiles().isEmpty());

        assertTrue(gameController.checkValidRetrieve("X",toCheckTrue));

        assertTrue(gameController.getSelectedTiles().containsAll(toCheckTrue) && (
                gameController.getSelectedTiles().size() == toCheckTrue.size())
        );

        assertFalse(gameController.checkValidRetrieve("X",toCheckFalse));

        assertTrue(gameController.getSelectedTiles().containsAll(toCheckTrue) && (
                gameController.getSelectedTiles().size() == toCheckTrue.size())
        );

        toCheckTrue.add(new Coordinates(5,1));

        assertTrue(gameController.checkValidRetrieve("X",toCheckTrue));

        assertTrue(gameController.getSelectedTiles().containsAll(toCheckTrue) && (
                gameController.getSelectedTiles().size() == toCheckTrue.size())
        );

        // EmptySlotException can be tested only after the test of moveTiles

    }

    @Test
    @DisplayName("moveTiles")
    void testMoveTiles() throws NicknameAlreadyUsedException, PlayersNumberOutOfRange, NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, RemoteException, NotAllPlayersHaveJoinedException, GameNotEndedException, GameNotStartedException, GameEndedException, InvalidCoordinatesException, EmptySlotException, PlayerNotInTurnException, NotEnoughSpaceException {
        Player X,Y;
        X = new Player("X");
        Y = new Player("Y");
        Game gameModel = new Game(2,X);
        gameModel.addPlayer(Y);
        GameController gameController = new GameController(gameModel);

        ArrayList<Coordinates> toCheckTrue = new ArrayList<>();
        ArrayList<Coordinates> toCheckFalse = new ArrayList<>();

        toCheckTrue.add(new Coordinates(4,1));
        toCheckFalse.add(new Coordinates(4,4));
        assertThrows( GameNotStartedException.class, () -> gameController.moveTiles("X", toCheckTrue,2) );

        gameModel.start();
        // set playerInTurn to X
        while(!gameModel.getPlayerInTurn().equals("X")) {
            gameModel.setPlayerTurn();
        }

        assertThrows( PlayerNotInTurnException.class, () -> gameController.moveTiles("Y",toCheckTrue,2));

        gameController.checkValidRetrieve("X",toCheckTrue);

        toCheckTrue.add(1,new Coordinates(5,1));

        assertThrows( InvalidCoordinatesException.class, () -> gameController.moveTiles("X",toCheckTrue,2));

        // board setup to check EmptySlotException
        gameController.checkValidRetrieve("X",toCheckTrue);
        gameController.moveTiles("X",toCheckTrue,2);
        gameModel.setPlayerTurn();

        assertThrows( EmptySlotException.class, () -> gameController.moveTiles("X",toCheckTrue,2));

        toCheckTrue.remove(0);
        toCheckTrue.remove(0);
        toCheckTrue.add(new Coordinates(3,2));
        toCheckTrue.add(new Coordinates(4,2));
        toCheckTrue.add(new Coordinates(5,2));

        assertTrue( gameController.checkValidRetrieve("X", toCheckTrue) );

        gameController.moveTiles("X",toCheckTrue,2);
        gameModel.setPlayerTurn();
        toCheckTrue.remove(0);
        toCheckTrue.remove(0);
        toCheckTrue.remove(0);
        toCheckTrue.add(new Coordinates(1,3));
        toCheckTrue.add(new Coordinates(1,4));

        assertTrue( gameController.checkValidRetrieve("X",toCheckTrue));

        assertThrows( NotEnoughSpaceException.class, () -> gameController.moveTiles("X",toCheckTrue,2));
    }

    @Test
    @DisplayName("postBroadcastMessage")
    void testPostBroadcastMessage() throws NicknameAlreadyUsedException, PlayersNumberOutOfRange, RemoteException, NegativeFieldException, IllegalFilePathException, NotEnoughCardsException {
        Player X,Y;
        X = new Player("X");
        Y = new Player("Y");
        Game gameModel = new Game(2,X);
        gameModel.addPlayer(Y);
        GameController gameController = new GameController(gameModel);

        assertThrows( InvalidPlayerException.class, () -> gameController.postBroadCastMessage("Z","HI"));
    }

    @Test
    @DisplayName("postDirectMessage")
    void testPostDirectMessage() throws NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, PlayersNumberOutOfRange, NicknameAlreadyUsedException, RemoteException {
        Player X,Y;
        X = new Player("X");
        Y = new Player("Y");
        Game gameModel = new Game(2,X);
        gameModel.addPlayer(Y);
        GameController gameController = new GameController(gameModel);

        assertThrows( InvalidPlayerException.class, () -> gameController.postDirectMessage("Z","X","HI"));
        assertThrows( InvalidPlayerException.class, () -> gameController.postDirectMessage("X","Z","HI"));
    }

    @Test
    @DisplayName("handleRejoinedPlayer")
    void testHandleRejoinedPlayer() throws NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, PlayersNumberOutOfRange, RemoteException {
        Player host = new Player("host"), x = new Player("x");
        Game game = new Game(2,host);
        GameController controller = new GameController(game);

        assertThrows(PlayerNotFoundException.class, () -> {
            controller.handleRejoinedPlayer("x");
        });
    }
    @Test
    @DisplayName("triggerHeartbeat, handleCrashedPlayer")
    void testTriggerHeartBeat() throws NegativeFieldException, IllegalFilePathException, NotEnoughCardsException, PlayersNumberOutOfRange, RemoteException, InterruptedException, NicknameAlreadyUsedException, GameNotStartedException, GameEndedException, InvalidCoordinatesException, EmptySlotException, PlayerNotInTurnException, NotAllPlayersHaveJoinedException, GameNotEndedException {
        Player host = new Player("host"), x = new Player("x");
        Game game = new Game(2,host);
        GameController controller = new GameController(game);
        ArrayList<Coordinates> coo = new ArrayList<>();

        controller.triggerHeartBeat("host");
        assertTrue(controller.getTimers().containsKey("host") && controller.getTimers().keySet().size() == 1);
        assertTrue(controller.getTimers().get("host").isScheduled());
        controller.triggerHeartBeat("host");
        assertTrue(controller.getTimers().get("host").isScheduled());
        game.addPlayer(x);
        game.start();
        if(game.getPlayerInTurn().equals("x")) {
            game.setPlayerTurn();
        }
        Thread.sleep(16000);
        assertTrue(game.getPlayerInTurn().equals("x"));

    }

}
