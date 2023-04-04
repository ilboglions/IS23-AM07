package it.polimi.ingsw.model.cards.common;


import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.tokens.ScoringToken;
import it.polimi.ingsw.model.tokens.TokenPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class CommonGoalCardTest {

    /**
     * Testing CommonGoalCard exceptions throw cases
     */
    @Test
    @DisplayName("CardCreationExceptionTest")
    void CommonGoalCardException() {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(0,"");});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(5,"");});
        assertThrows(NullPointerException.class, ()->{
            new MarioPyramid(3,null);});
    }

    /**
     * Tests getDescription method
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("getDescriptionTest")
    void getDescriptionTest() throws PlayersNumberOutOfRange {
        CommonGoalCard card = new MarioPyramid(3,"description");
        assertEquals("description", card.getDescription());
    }

    /**
     * Testing the filling of tokenStack when creating a CommonGoalCard
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("tokenStackfillingTester")
    void tokenStackTester () throws PlayersNumberOutOfRange {
        CommonGoalCard card = new MarioPyramid(2,"description");
        Stack<ScoringToken> tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertTrue(tokens.empty());
        card = new MarioPyramid(3,"description");
        tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.SIX, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertTrue(tokens.empty());
        card = new MarioPyramid(4,"description");
        tokens = card.getTokenStack();
        assertEquals(TokenPoint.EIGHT, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.SIX, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.FOUR, tokens.pop().getScoreValue());
        assertEquals(TokenPoint.TWO,tokens.pop().getScoreValue());
        assertTrue(tokens.empty());

    }
    @Test
    @DisplayName("popTokenTest")
    void popTokenToTest () throws PlayersNumberOutOfRange, TokenAlreadyGivenException {
        CommonGoalCard card = new MarioPyramid(4,"description");
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
