package it.polimi.ingsw.model.chat;

import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions(){
        Chat test = new Chat();

        assertThrows(NullPointerException.class, ()->{
            test.postMessage(null);
        });
        assertThrows(NullPointerException.class, ()->{
            test.getPlayerMessages(null);
        });
        assertThrows(SenderEqualsRecipientException.class, ()->{
            test.postMessage(new Message("test", "test", "test message"));
        });
    }

    @Test
    @DisplayName("Test all the methods")
    void testMethods() throws SenderEqualsRecipientException {
        Chat test = new Chat();

        assertEquals(0, test.getPlayerMessages("noUser").size());

        test.postMessage(new Message("testUser", "recipientUser", "Test message"));
        assertEquals(1, test.getPlayerMessages("testUser").size());
        assertEquals(1, test.getPlayerMessages("recipientUser").size());

        test.postMessage(new Message("testUser", "Global message"));
        assertEquals(2, test.getPlayerMessages("testUser").size());
        assertEquals(2, test.getPlayerMessages("recipientUser").size());
    }

}