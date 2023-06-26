package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.GameState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class GameListenerTest {
    /**
     * Test to call all the methods of the listener
     */
    @Test
    @DisplayName("Test all the methods")
    void fullTest(){
        GameListener test = new GameListener();
        GeneralSubscriber sub1 = new GeneralSubscriber("sub1");
        GeneralSubscriber sub2 = new GeneralSubscriber("sub2");

        test.addSubscriber(sub1);
        test.addSubscriber(sub2);

        test.notifyPlayerInTurn("sub1");
        test.notifyChangedGameState(GameState.STARTED);
        test.notifyTurnOrder(new ArrayList<>());
        test.onPlayerWins("sub1", 15, new HashMap<>());
        test.onCommonCardDraw("sub1", new ArrayList<>());
        test.notifyAlreadyJoinedPlayers(new HashSet<>(), "sub1");
        test.onPlayerJoinGame("sub2");
        test.notifyPlayerCrashed("sub1");
        test.onCommonCardStateChange(new ArrayList<>());
    }
}