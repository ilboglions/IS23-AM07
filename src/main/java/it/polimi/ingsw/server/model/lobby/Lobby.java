package it.polimi.ingsw.server.model.lobby;

import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.exceptions.*;

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

        Optional<Player> op =  waitingPlayers.stream().filter(p -> p.getUsername().equals(playerName)).findFirst();
        if ( op.isPresent() ){
            Game result = games.stream()
                    .filter(tmp -> !tmp.isStarted())
                    .findFirst()
                    .orElseThrow(()-> new NoAvailableGameException("All the games have already started"));

            result.addPlayer(op.get());

            waitingPlayers.remove(op.get());

            return result;

        }
        else {
            throw new InvalidPlayerException("Player is null");
        }


    }

    /**
     * creates a new game
     * @param nPlayers the number of player of the game
     * @param hostNickname the host player of the game
     * @return the Game instance
     * @throws PlayersNumberOutOfRange if the player's number is out of the specific range
     * @throws BrokenInternalGameConfigurations if some internal configurations are broken
     * @throws InvalidPlayerException if the host player cannot be found
     */
    public Game createGame(int nPlayers, String hostNickname) throws PlayersNumberOutOfRange, BrokenInternalGameConfigurations, InvalidPlayerException {
        Objects.requireNonNull(hostNickname);

        Optional<Player> host =  waitingPlayers.stream().filter(p -> p.getUsername().equals(hostNickname)).findFirst();
        if(host.isEmpty())
            throw new InvalidPlayerException("Player is null");
        try{
            Game newGame = new Game(nPlayers, host.get());
            waitingPlayers.remove(host.get());
            games.add(newGame);
            return newGame;
        }  catch (NegativeFieldException | IllegalFilePathException | NotEnoughCardsException e) {
            throw new BrokenInternalGameConfigurations();
        }

    }

    /**
     * create a new player
     * @param nickname the nickname to be assigned to the player
     * @throws NicknameAlreadyUsedException if the nickname has been already chosen by another player
     * @throws InvalidPlayerException if the username string is null, empty or longer than 15 characters
     */
    public void createPlayer(String nickname) throws NicknameAlreadyUsedException, InvalidPlayerException  {
        if(nickname == null)
            throw new InvalidPlayerException("Player is null");
        else if (nickname.isBlank())
            throw new InvalidPlayerException("Nickname is blank");
        else if(nickname.length() >= 15)
            throw new InvalidPlayerException("Nickname is too long");


        for(Game game : games) {
            if(game.searchPlayer(nickname).isPresent())
                throw new NicknameAlreadyUsedException("This nickname is already used");
        }

        if (waitingPlayers.contains(new Player(nickname)))
                throw new NicknameAlreadyUsedException("This nickname is already used");

        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }


    /**
     * This method removes a crashed player from the waitingPlayers list if it's present
     * @param username username of the crashed player
     * @throws PlayerNotFoundException if the player is not present in the lobby
     */
    public void handleCrashedPlayer(String username) throws PlayerNotFoundException {
        Objects.requireNonNull(username);

        if(this.waitingPlayers.contains(new Player(username)))
            this.waitingPlayers.remove(new Player(username));
        else
            throw new PlayerNotFoundException("There isn't a player with this username in the lobby");
    }

    /**
     * removes the game from the list of active games
     * @param game the game to be removed
     */
    public void removeGame(GameModelInterface game) {
        this.games.remove((Game) game);
    }
}
