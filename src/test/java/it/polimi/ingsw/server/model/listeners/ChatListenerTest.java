package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.chat.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class ChatListenerTest {

    /**
     * Test to call all the methods of the listener
     */
    @Test
    @DisplayName("Test all the methods")
    void fullTest(){
        ChatListener test = new ChatListener();
        GeneralSubscriber sub1 = new GeneralSubscriber("sub1");
        GeneralSubscriber sub2 = new GeneralSubscriber("sub2");

        test.addSubscriber(sub1);
        test.addSubscriber(sub2);

        test.onNewMessage(new Message("sub1", "test"));
        test.onNewMessage(new Message("sub1", "sub2", "test"));

        test.removeSubscriber("sub1");
    }
}