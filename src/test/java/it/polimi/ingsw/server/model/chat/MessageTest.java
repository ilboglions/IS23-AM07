package it.polimi.ingsw.server.model.chat;

import it.polimi.ingsw.server.model.chat.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test Message class
 */
class MessageTest {

    /**
     * This tests that all the exception are firing correctly for null values
     */
    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions(){
        assertThrows(NullPointerException.class, ()->{
           Message test = new Message(null, "", "");
        });
        assertThrows(NullPointerException.class, ()->{
            Message test = new Message("", null, "");
        });
        assertThrows(NullPointerException.class, ()->{
            Message test = new Message("", "", null);
        });
        assertThrows(NullPointerException.class, ()->{
            Message test = new Message("", null);
        });
        assertThrows(NullPointerException.class, ()->{
            Message test = new Message(null, "");
        });
    }

    /**
     * This tests if the message are created correctly, both for private and global messages
     */
    @Test
    @DisplayName("Test all the methods")
    void testMethods(){
        Message test = new Message("testUser", "receiverUser", "Test message");

        assertEquals("testUser", test.getSender());
        assertTrue(test.getRecipient().isPresent() && test.getRecipient().get().equals("receiverUser"));
        assertEquals("Test message", test.getContent());

        test = new Message("testUser", "Test message");
        assertTrue(test.getRecipient().isEmpty());
    }

}