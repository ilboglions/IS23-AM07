package it.polimi.ingsw.server.model.chat;

import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.listeners.ChatListener;
import it.polimi.ingsw.remoteInterfaces.ChatSubscriber;

import java.util.ArrayList;

/**
 * The Chat class stores all the messages sent by all the player inside a game, either in public or in private chat
 */
public class Chat {
    /**
     * The list of all the messages sent by the players
     */
    private final ArrayList<Message> sentMessages;
    /**
     * The listener that will be updated on action
     */
    private final ChatListener chatListener;

    /**
     * Constructor of the chat initialize the arraylist of messages and the listener
     */
    public Chat() {
        sentMessages = new ArrayList<>();
        chatListener = new ChatListener();
    }

    /**
     * Add a ChatSubscriber to the chatListener
     * @param subscriber new ChatSubscriber to be added
     */
    public void subscribeToListener(ChatSubscriber subscriber){
        chatListener.addSubscriber(subscriber);
    }

    /**
     * This method is used to add a message to the chat message list
     * @param msg is the message object that will be added to the list
     * @throws SenderEqualsRecipientException if the recipient and the sender of the message is the same user
     */
    public void postMessage(Message msg) throws SenderEqualsRecipientException {
        if(msg == null) {
            throw new NullPointerException("Message pointer is null");
        } else if ( msg.getRecipient().isPresent() && msg.getRecipient().get().equals(msg.getSender()) ) {
            // if the sender is also the recipient
            throw new SenderEqualsRecipientException("Sender and recipient are the same player!!");
        }
        sentMessages.add(msg);
        chatListener.onNewMessage(msg);
    }

    /**
     * This is the method to retrieve all the messages relative to a player, either sent and received
     * @param player a String that represent the username of the player for which to search the messages
     * @return an Arraylist of messages relative to the player
     */
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


    /**
     * Remove a user from the ChatListener's list
     * @param username username of the player to be removed
     */
    public void unsubscribeFromListener(String username) {
        this.chatListener.removeSubscriber(username);
    }

    protected ChatListener getChatListener() {
        return chatListener;
    }
}
