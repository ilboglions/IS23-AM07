package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.lobby.exceptions.NicknameAlreadyUsedException;
import it.polimi.ingsw.model.lobby.exceptions.NoAvailableGameException;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;

public class Lobby {
    private final ArrayList<Player> waitingPlayers;
    private final ArrayList<Game> games;

    public Lobby() {
        this.waitingPlayers = new ArrayList<Player>();
        this.games = new ArrayList<Game>();
    }

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

    public Game createGame(int nPlayers, Player host) {
        Game newGame = new Game(nPlayers, host);

        games.add(newGame);

        return newGame;
    }

    public void createPlayer(String nickname) throws NicknameAlreadyUsedException {
        for(Game game : games) {
            if(game.searchPlayer(nickname).isPresent())
                throw new NicknameAlreadyUsedException("This nickname is already used");
        }

        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }
}
