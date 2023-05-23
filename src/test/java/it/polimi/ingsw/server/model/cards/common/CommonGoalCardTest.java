package it.polimi.ingsw.server.model.cards.common;


import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.TokenAlreadyGivenException;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.tokens.TokenPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.util.EmptyStackException;
import java.util.Stack;

public class CommonGoalCardTest {

    /**
     * Testing CommonGoalCard exceptions throw cases
     */
    @Test
    @DisplayName("CardCreationExceptionTest")
    void CommonGoalCardException() {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(0,"", CommonCardType.CORNERS);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(5,"", CommonCardType.CORNERS);});
        assertThrows(NullPointerException.class, ()->{
            new MarioPyramid(3,null, CommonCardType.CORNERS);});
        assertThrows(NullPointerException.class, ()->{
            new MarioPyramid(3,"", null);});
    }

    /**
     * Tests getDescription method
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("getDescriptionTest")
    void getDescriptionTest() throws PlayersNumberOutOfRange, RemoteException {
        CommonGoalCard card = new MarioPyramid(3,"description", CommonCardType.CORNERS);
        assertEquals("description", card.getDescription());
    }

    /**
     * Testing the filling of tokenStack when creating a CommonGoalCard
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("tokenStackfillingTester")
    void tokenStackTester () throws PlayersNumberOutOfRange, RemoteException {
        CommonGoalCard card = new MarioPyramid(2,"description", CommonCardType.CORNERS);
        Stack<ScoringToken> tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertTrue(tokens.empty());
        card = new MarioPyramid(3,"description", CommonCardType.CORNERS);
        tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.SIX, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertTrue(tokens.empty());
        card = new MarioPyramid(4,"description", CommonCardType.CORNERS);
        tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.SIX, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.TWO,tokens.pop().getScoreValue());
        assertTrue(tokens.empty());

    }
    @Test
    @DisplayName("popTokenTest")
    void popTokenToTest () throws PlayersNumberOutOfRange, TokenAlreadyGivenException, RemoteException {
        CommonGoalCard card = new MarioPyramid(4,"description", CommonCardType.CORNERS);
        assertEquals(TokenPoint.EIGHT, card.popTokenTo("Player1").getScoreValue());
        assertThrows(TokenAlreadyGivenException.class, () ->{
            card.popTokenTo("Player1");
        });
        assertEquals(TokenPoint.SIX, card.popTokenTo("Player2").getScoreValue());
        assertEquals(TokenPoint.FOUR, card.popTokenTo("Player3").getScoreValue());
        assertEquals(TokenPoint.TWO, card.popTokenTo("Player4").getScoreValue());
        assertThrows(EmptyStackException.class, () ->{
            card.popTokenTo("Player5");
        });

    }



}
