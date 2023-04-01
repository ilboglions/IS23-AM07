package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.*;
import java.util.ArrayList;
import java.util.Objects;

import static it.polimi.ingsw.model.utilities.UtilityFunctions.findAdjacentElements;

public class Player {
    private final String username;
    private PersonalGoalCard personalCard;
    private final PlayerBookshelf bookshelf;
    private final ArrayList<ScoringToken> tokenAcquired;
    private int points;

    public Player(String username) {
        this.username = username;
        this.bookshelf = new PlayerBookshelf();
        this.tokenAcquired = new ArrayList<>();
        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public PersonalGoalCard getPersonalCard() {
        return personalCard;
    }

    public PlayerBookshelf getBookshelf() {
        return bookshelf;
    }

    public ArrayList<ScoringToken> getTokenAcquired() {
        return tokenAcquired;
    }

    public int getPoints() {
        return points;
    }

    private int calculatePointsPersonalGoalCard() {

        int count = bookshelf.nElementsOverlapped(personalCard.getBookshelf());

        return personalCard.getPointsReference().get(count);
    }

    private int calculatePointsAdjacent(Bookshelf bookshelf, Map<Integer, Integer> adjacentPointReference) {
        Map<ItemTile, List<List<Coordinates>>> totalGroups = searchAdjacentGroups(bookshelf, Collections.min(adjacentPointReference.keySet()));
        int totalPoints = 0;

        for (List<List<Coordinates>> groups : totalGroups.values()) {
            for (List<Coordinates> group : groups) {
                if (group.size() >= Collections.max(adjacentPointReference.keySet()) ) {
                    totalPoints += adjacentPointReference.get(Collections.max(adjacentPointReference.keySet()));
                } else {
                    totalPoints += adjacentPointReference.getOrDefault(group.size(), 0);
                }
            }
        }

        return totalPoints;
    }

    private Map<ItemTile, List<List<Coordinates>>> searchAdjacentGroups(Bookshelf bookshelf, int minGroupSize) {
        Map<ItemTile, List<List<Coordinates>>> result = new HashMap<>();
        Set<Coordinates> visited = new HashSet<>();

        for (ItemTile itemTile : ItemTile.values()) {
            List<List<Coordinates>> groups = new ArrayList<>();

            for (int i = 0; i < bookshelf.getRows(); i++) {
                for (int j = 0; j < bookshelf.getColumns(); j++) {
                    try {
                        if (bookshelf.getItemTile(new Coordinates(i,j)).isPresent() && bookshelf.getItemTile(new Coordinates(i,j)).get().equals(itemTile) && !visited.contains(new Coordinates(i,j))) {
                            List<Coordinates> adjacencyGroup =  findAdjacentElements(bookshelf, i, j, itemTile, visited);

                            if (adjacencyGroup.size() >= minGroupSize) {
                                groups.add(adjacencyGroup);
                            }
                        }
                    } catch (InvalidCoordinatesException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            result.put(itemTile, groups);
        }
        return result;
    }



    public int updatePoints(Map<Integer, Integer> adjacentPointReference) {
        int total = 0;

        for(ScoringToken token : tokenAcquired) {
            total += token.getScoreValue().getValue();
        }

        total += calculatePointsPersonalGoalCard();
        total += calculatePointsAdjacent(bookshelf, adjacentPointReference);
        this.points = total;

        return this.points;
    }

    public void addToken(ScoringToken token) {
        this.tokenAcquired.add(token);
    }

    public void assignPersonalCard(PersonalGoalCard card) {
        this.personalCard = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
