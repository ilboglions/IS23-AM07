package it.polimi.ingsw.server.model.lobby;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.exceptions.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

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
     * @param playerName the player to be added to the game
     * @throws NoAvailableGameException if no game is available
     * @throws InvalidPlayerException if the player is not in waiting list or the player nickname is already taken in the game
     * @throws NicknameAlreadyUsedException if there is already a user in the lobby or in any game with the same username
     * @throws PlayersNumberOutOfRange if the number of players is less than 2 or greater than 4
     * @return the Game in which the player has joined
     */
    public Game addPlayerToGame(String playerName) throws NoAvailableGameException, InvalidPlayerException, NicknameAlreadyUsedException, PlayersNumberOutOfRange {
        Objects.requireNonNull(playerName);

        Game result = games.stream()
                            .filter(tmp -> !tmp.getIsStarted())
                            .findFirst()
                            .orElseThrow(()-> new NoAvailableGameException("All the games have already started"));

        Optional<Player> op =  waitingPlayers.stream().filter(p -> p.getUsername().equals(playerName)).findFirst();
        if ( op.isPresent() ){
            result.addPlayer(op.get());
        }
        else {
            throw new InvalidPlayerException();
        }

        waitingPlayers.remove(op.get());

        return result;
    }

    /**
     * creates a new game
     * @param nPlayers the number of player of the game
     * @param hostNickname the host player of the game
     * @return the Game instance
     * @throws PlayersNumberOutOfRange if the player's number is out of the specific range
     * @throws BrokenInternalGameConfigurations if some internal configurations are broken
     * @throws InvalidPlayerException if the host player cannot be found
     * @throws NotEnoughCardsException if the number of cards required is grater than the number of cards available to draw
     */
    public Game createGame(int nPlayers, String hostNickname) throws PlayersNumberOutOfRange, BrokenInternalGameConfigurations, InvalidPlayerException, NotEnoughCardsException {
        Objects.requireNonNull(hostNickname);

        Optional<Player> host =  waitingPlayers.stream().filter(p -> p.getUsername().equals(hostNickname)).findFirst();
        if(host.isEmpty())
            throw new InvalidPlayerException();
        try{
            Game newGame = new Game(nPlayers, host.get());
            waitingPlayers.remove(host.get());
            games.add(newGame);
            return newGame;
        }  catch (NegativeFieldException | FileNotFoundException e) {
            throw new BrokenInternalGameConfigurations();
        }

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

        if (waitingPlayers.contains(new Player(nickname)))
                throw new NicknameAlreadyUsedException("This nickname is already used");

        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }


}
