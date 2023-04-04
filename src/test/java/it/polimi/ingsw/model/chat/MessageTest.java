package it.polimi.ingsw.model.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

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
    }

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