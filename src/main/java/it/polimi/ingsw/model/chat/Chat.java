package it.polimi.ingsw.model.chat;

import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.listeners.ChatListener;
import java.util.ArrayList;

public class Chat {

    private final ArrayList<Message> sentMessages;
    private final ChatListener chatListener;

    public Chat() {
        sentMessages = new ArrayList<>();
        chatListener = new ChatListener();
    }

    public void postMessage(Message msg) throws SenderEqualsRecipientException {
        if(msg == null) {
            throw new NullPointerException("Message pointer is null");
        } else if ( msg.getRecipient().isPresent() && msg.getRecipient().get().equals(msg.getSender()) ) {
            // if the sender is also the recipient
            throw new SenderEqualsRecipientException("Sender and recipient are the same player!!");
        }
        sentMessages.add(msg);
        chatListener.onNewMessage(msg);
        // this is to be discussed, we always said we did not want to expose the rep, so should we send a sort of copy?
        // since we want to maintain a chronological order of the messages, we can use add. add puts the new element at the end of the list
    }


    public ArrayList<Message> getPlayerMessages(String player) {
        ArrayList<Message> playerMsgs = new ArrayList<>();
        if(player == null)
            throw new NullPointerException("Player string is null");
        for(Message msg : sentMessages) {
            // if the message is on broadcast, or the recipient or the sender is the requested player
            if( msg.getRecipient().isEmpty() || (msg.getRecipient().isPresent() && msg.getRecipient().get().equals(player)) || msg.getSender().equals(player) ) {
                playerMsgs.add(msg);
                // using add makes it possible to have the messages ordered in chronological order
            }
        }
        return playerMsgs;
    }


}
