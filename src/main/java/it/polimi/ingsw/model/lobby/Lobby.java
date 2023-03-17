package it.polimi.ingsw.model.lobby;

import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;

public class Lobby {
    private final ArrayList<Player> waitingPlayers;
    private final ArrayList<Game> games;

    public Lobby() {
        this.waitingPlayers = new ArrayList<Player>();
        this.games = new ArrayList<Game>();
    }

    public boolean addPlayerToGame(Player player) {
        Game result = games.stream()
                            .filter(tmp -> tmp.getIsStarted())
                            .findFirst()
                            .orElse(null);

        if(result == null){
            result = this.createGame();
        }

        try {
            result.addPlayer(player);
        }
        catch(FullGameException e) {
            return false;
        }

        waitingPlayers.remove(player);
        return true;
    }

    public Game createGame() {
        Game newGame = new Game();

        games.add(newGame);

        return newGame;
    }

    public boolean createPlayer(String nickname) {
        Player newPlayer = new Player(nickname);

        waitingPlayers.add(newPlayer);
    }
}
