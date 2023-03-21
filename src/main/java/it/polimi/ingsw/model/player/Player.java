package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.*;

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
        int count = 0;
        Coordinates c;

        for(int i=0; i<bookshelf.getRows(); i++) {
            for(int j=0; j<bookshelf.getColumns(); j++) {
                c = new Coordinates(i,j);
                if(personalCard.getBookshelf().getItemTile(c).isPresent() && bookshelf.getItemTile(c).equals(personalCard.getBookshelf().getItemTile(c)))
                    count++;
            }
        }

        return personalCard.getPointsReference().get(count);
    }

    private int calculatePointsAdjacent(Bookshelf bookshelf, Map<Integer, Integer> adjacentPointReference) {
        Map<ItemTile, List<List<Coordinates>>> totalGroups = searchAdjacentGroups(bookshelf);
        int totalPoints = 0;

        for (List<List<Coordinates>> groups : totalGroups.values()) {
            for (List<Coordinates> group : groups) {
                if (group.size() >= 6) {
                    totalPoints += adjacentPointReference.get(6);
                } else {
                    totalPoints += adjacentPointReference.getOrDefault(group.size(), 0);
                }
            }
        }

        return totalPoints;
    }

    private static Map<ItemTile, List<List<Coordinates>>> searchAdjacentGroups(Bookshelf bookshelf) {
        Map<ItemTile, List<List<Coordinates>>> result = new HashMap<>();
        Set<Coordinates> visited = new HashSet<>();

        for (ItemTile itemTile : ItemTile.values()) {
            List<List<Coordinates>> groups = new ArrayList<>();

            for (int i = 0; i < bookshelf.getRows(); i++) {
                for (int j = 0; j < bookshelf.getColumns(); j++) {
                    if (bookshelf.getItemTile(new Coordinates(i,j)).isPresent() && bookshelf.getItemTile(new Coordinates(i,j)).get().equals(itemTile) && !visited.contains(new Coordinates(i,j))) {
                        List<Coordinates> adjacencyGroup = new ArrayList<>();

                        recursiveSearch(bookshelf, i, j, itemTile, adjacencyGroup, visited);

                        if (adjacencyGroup.size() >= 3) {
                            groups.add(adjacencyGroup);
                        }
                    }
                }
            }
            result.put(itemTile, groups);
        }
        return result;
    }

    private static void recursiveSearch(Bookshelf bookshelf, int row, int col, ItemTile itemTile, List<Coordinates> group, Set<Coordinates> visited) {
        if (row < 0 || row >= bookshelf.getRows() || col < 0 || col >= bookshelf.getColumns() || visited.contains(new Coordinates(row,col)) || bookshelf.getItemTile(new Coordinates(row,col)).isEmpty()
                || (bookshelf.getItemTile(new Coordinates(row,col)).isPresent() && !bookshelf.getItemTile(new Coordinates(row,col)).get().equals(itemTile))) {
            return;
        }

        group.add(new Coordinates(row, col));
        visited.add(new Coordinates(row,col));

        recursiveSearch(bookshelf, row + 1, col, itemTile, group, visited);
        recursiveSearch(bookshelf, row, col + 1, itemTile, group, visited);
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
}
