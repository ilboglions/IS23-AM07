package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.lobby.exceptions.BrokenInternalGameConfigurations;
import it.polimi.ingsw.model.lobby.exceptions.NicknameAlreadyUsedException;
import it.polimi.ingsw.model.lobby.exceptions.NoAvailableGameException;
import it.polimi.ingsw.model.lobby.exceptions.NoAvailablePlayerException;
import it.polimi.ingsw.model.player.Player;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
     * @throws NoAvailablePlayerException if the player is not in waiting list or the player nickname is already taken in the game
     *
     */
    public void addPlayerToGame(String playerName) throws NoAvailableGameException, NoAvailablePlayerException {
        Game result = games.stream()
                            .filter(tmp -> !tmp.getIsStarted())
                            .findFirst()
                            .orElseThrow(()-> new NoAvailableGameException("All the games have already started"));

        Optional<Player> op =  waitingPlayers.stream().filter(p -> p.getUsername().equals(playerName)).findFirst();
        boolean valid;
        if ( op.isPresent() ){
            valid = result.addPlayer(op.get());
        }
        else {
            throw new NoAvailablePlayerException();
        }

        if(!valid)
            throw new NoAvailablePlayerException();

        waitingPlayers.remove(op.get());


    }

    /**
     * creates a new game
     * @param nPlayers the number of player of the game
     * @param hostNickname the host player of the game
     * @return the Game instance
     * @throws PlayersNumberOutOfRange if the player's number is out of the specific range
     * @throws BrokenInternalGameConfigurations if some internal configurations are broken
     * @throws NoAvailablePlayerException if the host player cannot be found
     *
     */
    public Game createGame(int nPlayers, String hostNickname) throws PlayersNumberOutOfRange, BrokenInternalGameConfigurations, NoAvailablePlayerException {

        Optional<Player> host =  waitingPlayers.stream().filter(p -> p.getUsername().equals(hostNickname)).findFirst();
        if( host.isEmpty())
            throw new NoAvailablePlayerException();
        try{
            Game newGame = new Game(nPlayers, host.get());
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

        if ( waitingPlayers.contains(new Player(nickname)) )
                throw new NicknameAlreadyUsedException("This nickname is already used");

        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }
}
