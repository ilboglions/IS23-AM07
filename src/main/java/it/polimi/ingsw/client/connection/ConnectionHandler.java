package it.polimi.ingsw.client.connection;
// da spostare le coordinates in un altro package (?)
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.util.ArrayList;

public interface ConnectionHandler {

    void init();
    void close();
    void JoinLobby(String username);

    void CreateGame(int nPlayers);

    void JoinGame();

    void checkValidRetrieve(ArrayList<Coordinates> tiles);

    void moveTiles( ArrayList<Coordinates> tiles, int column);


}
