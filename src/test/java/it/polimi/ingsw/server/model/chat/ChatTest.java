package it.polimi.ingsw.server.model.chat;

import it.polimi.ingsw.remoteInterfaces.ChatSubscriber;
import it.polimi.ingsw.server.model.exceptions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test Chat class
 */
class ChatTest {

    /**
     * This tests that all the exception are firing correctly, such as null values or equal sender and recipient
     */
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

    /**
     * This tests if all the methods to post and get messages are working correctly, with no messages, private messages or global messages
     * @throws SenderEqualsRecipientException if the sender and the recipient are equal
     */
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

    @Test
    @DisplayName("GameController subscribeToListener LivingRoomBoard")
    void testSubscribeToListener() {
        Chat chat = new Chat();
        SubscriberForTest sub = new SubscriberForTest();

        chat.subscribeToListener((ChatSubscriber) sub);
        assertTrue(chat.getChatListener().getSubscribers().contains(sub) && chat.getChatListener().getSubscribers().size() == 1);
    }

    private class SubscriberForTest implements ChatSubscriber {

        @Override
        public String getSubscriberUsername() throws RemoteException {
            return null;
        }

        @Override
        public void receiveMessage(String from, String recipient, String msg) throws RemoteException {

        }

        @Override
        public void receiveMessage(String from, String msg) throws RemoteException {

        }
    }




}