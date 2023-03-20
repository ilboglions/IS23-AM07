package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.lobby.exceptions.NicknameAlreadyUsedException;
import it.polimi.ingsw.model.lobby.exceptions.NoAvailableGameException;
import it.polimi.ingsw.model.player.Player;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * the lobby is where players can be assigned to game which are not already started. It contains a reference to all the games that are instanced and the players
 * which are waiting to join them
 */
public class Lobby {
    /**
     * the list of waiting players
     */
    private final ArrayList<Player> waitingPlayers;
    /**
     * the list of all instanced games
     */
    private final ArrayList<Game> games;

    /**
     * the constructor of the class, it creates the collections needed
     */
    public Lobby() {
        this.waitingPlayers = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    /**
     * the method add a player to one of the available games
     * @param player the player to be added to the game
     * @return  true, if the player has a valid username and can be added to the game, false in other cases
     * @throws NoAvailableGameException if no game is available
     */
    public boolean addPlayerToGame(Player player) throws NoAvailableGameException {
        Game result = games.stream()
                            .filter(tmp -> !tmp.getIsStarted())
                            .findFirst()
                            .orElseThrow(()-> new NoAvailableGameException("All the games have already started"));

        boolean valid = result.addPlayer(player);

        if(!valid)
            return false;

        waitingPlayers.remove(player);
        return true;

    }

    /**
     * creates a new game
     * @param nPlayers the number of player of the game
     * @param host the host player of the game
     * @return the Game instance
     * @throws FileNotFoundException if the configurations files of the games can not be found
     */
    public Game createGame(int nPlayers, Player host) throws FileNotFoundException {
        Game newGame = new Game(nPlayers, host);

        games.add(newGame);

        return newGame;
    }

    /**
     * create a new player
     * @param nickname the nickname to be assigned to the player
     * @throws NicknameAlreadyUsedException if the nickname has been already chosen by another player
     */
    public void createPlayer(String nickname) throws NicknameAlreadyUsedException {
        for(Game game : games) {
            if(game.searchPlayer(nickname).isPresent())
                throw new NicknameAlreadyUsedException("This nickname is already used");
        }

        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }
}
