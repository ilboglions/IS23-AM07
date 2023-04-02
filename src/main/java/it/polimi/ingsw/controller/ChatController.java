package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.chat.Chat;
import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.game.GameChatInterface;

import java.util.Optional;

public class ChatController {
    /**
     * the chat to be controlled
     */
    private final GameChatInterface chat;

    /**
     * constructor of the class, assign the chat model to the controller
     * @param chat the chat model to be assigned
     */
    public ChatController(GameChatInterface chat) {
        this.chat = chat;
    }

    /**
     * used to create a broadcast message
     * @param player the player that will be post the message
     * @param message the message to be posted
     * @return true, if the message can be posted, false otherwise
     */
    public boolean postBroadCastMessage(String player, String message){
        try {
            chat.postMessage(player,Optional.empty(),message);
        } catch (SenderEqualsRecipientException e) {
            return false;
        } catch (InvalidPlayerException e) {
            return false;
        }

        return true;
    }

    /**
     * used to create a message to a certain player in the same game
     * @param player the player that want to send the message
     * @param receiver the player that will receive the message
     * @param message the message to be sent
     * @return false, if the message cannot be posted or sent to the receiver, true otherwise
     */
    public boolean postDirectMessage(String player, String receiver,String message){

        try {
            chat.postMessage(player,Optional.of(receiver),message);
        } catch (SenderEqualsRecipientException e) {
            return false;
        } catch (InvalidPlayerException e) {
            return false;
        }

        return true;
    }




}
