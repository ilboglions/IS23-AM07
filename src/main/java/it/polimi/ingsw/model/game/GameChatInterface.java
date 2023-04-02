package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.model.exceptions.InvalidPlayerException;

import java.util.ArrayList;
import java.util.Optional;

public interface GameChatInterface {
    ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException;


    void postMessage(String sender, Optional<String> reciver, String message) throws SenderEqualsRecipientException, InvalidPlayerException;
}
