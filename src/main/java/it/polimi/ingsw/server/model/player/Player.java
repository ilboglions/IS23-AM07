package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.server.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.listeners.PlayerListener;
import it.polimi.ingsw.server.model.subscriber.PlayerSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.utilities.UtilityFunctions;

import java.util.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The player class represent a single player globally with also attributes used inside a game such as PlayerBookshelf, TokenAcquired and PersonalGoalCard
 */
public class Player {
    /**
     * The username of the user
     */
    private final String username;
    /**
     * The PersonalGoalCard assigned to the player in a specific Game
     */
    private PersonalGoalCard personalCard;
    /**
     * The PlayerBookshelf that is used by the player inside the Game
     */
    private final PlayerBookshelf bookshelf;
    /**
     * A list that represent the token acquired relative to the common goal of the game
     */
    private final ArrayList<ScoringToken> tokenAcquired;
    /**
     * The total points scored by the user with the PersonalGoalCard, CommonGoalCard and adjacency inside the PlayerBookshelf
     */
    private int points;
    /**
     * The listener used to notify the subscribers about the players changes
     */
    private final PlayerListener playerListener;

    /**
     * Constructor of the player
     * @param username is the username used by the player
     */
    public Player(String username) {
        this.playerListener = new PlayerListener();
        this.username = Objects.requireNonNull(username);
        this.bookshelf = new PlayerBookshelf(this);
        this.tokenAcquired = new ArrayList<>();
        this.points = 0;
    }

    /**
     * permits to subscribe a PlayerSubscriber to the player notifications
     * @param subscriber the interested subscriber
     */
    public void subscribeToListener(PlayerSubscriber subscriber){
        this.playerListener.addSubscriber(subscriber);
    }
    /**
     * Method used to retrieve the username
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method used to retrieve the PersonalGoalCard
     * @return the PersonalGoalCard of the player
     */
    public PersonalGoalCard getPersonalCard() {
        return personalCard;
    }

    /**
     * Method used to retrieve the PlayerBookshelf
     * @return the PlayerBookshelf of the player
     */
    public PlayerBookshelf getBookshelf() {
        return bookshelf;
    }

    /**
     * Method used to retrieve the list of the ScoringToken acquired
     * @return the list of the ScoringToken acquired of the player
     */
    public ArrayList<ScoringToken> getTokenAcquired() {
        return tokenAcquired;
    }

    /**
     * Method used to retrieve the total points scored
     * @return the points of the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * The method used to calculate the points scored with the PersonalGoalCard
     * @return the number of points
     */
    private int calculatePointsPersonalGoalCard() {

        int count = bookshelf.nElementsOverlapped(personalCard.getBookshelf());

        if(count == 0)
            return count;
        else if(count >= Collections.max(personalCard.getPointsReference().keySet())) {
            count = Collections.max(personalCard.getPointsReference().keySet());
        }

        return personalCard.getPointsReference().get(count);
    }

    /**
     * Method used to calculate all the points relative to the adjacency groups inside the PlayerBookshelf
     * @param bookshelf the PlayerBookshelf to calculate adjacency on
     * @param adjacentPointReference the Map with as key the number of tiles in a group and as values the points relative to each one
     * @return the total points scored
     */
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

    /**
     * Method used to search for all the adjacency groups inside the PlayerBookshelf, with a minimum of minGroupTiles tiles for group
     * @param bookshelf the PlayerBookshelf to calculate adjacency on
     * @param minGroupSize the minimum size of group that counts for the points
     * @return for each ItemTile a list of all the groups of adjacent tiles of that type
     */
    private Map<ItemTile, List<List<Coordinates>>> searchAdjacentGroups(Bookshelf bookshelf, int minGroupSize) {
        Map<ItemTile, List<List<Coordinates>>> result = new HashMap<>();
        Set<Coordinates> visited = new HashSet<>();

        for (ItemTile itemTile : ItemTile.values()) {
            List<List<Coordinates>> groups = new ArrayList<>();

            for (int i = 0; i < bookshelf.getRows(); i++) {
                for (int j = 0; j < bookshelf.getColumns(); j++) {
                    try {
                        if (bookshelf.getItemTile(new Coordinates(i,j)).isPresent() && bookshelf.getItemTile(new Coordinates(i,j)).get().equals(itemTile) && !visited.contains(new Coordinates(i,j))) {
                            List<Coordinates> adjacencyGroup =  UtilityFunctions.findAdjacentElements(bookshelf, i, j, itemTile, visited);

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


    /**
     * The method used to calculate the total points scored with PersonalGoalCard, CommonGoalCard and the adjacency inside the PlayerBookshelf
     * @param adjacentPointReference is the Map that represent for each size of group the relative points
     * @return the total points scored by the player
     */
    public int updatePoints(Map<Integer, Integer> adjacentPointReference) {
        int delta;
        Objects.requireNonNull(adjacentPointReference);
        if(adjacentPointReference.isEmpty())
            throw new IllegalArgumentException("You passed an empty Map for adjacentPointReference");

        int total = 0;

        for(ScoringToken token : tokenAcquired) {
            total += token.getScoreValue().getValue();
        }

        total += calculatePointsPersonalGoalCard();
        total += calculatePointsAdjacent(bookshelf, adjacentPointReference);
        delta = total - this.points;
        this.points = total;
        playerListener.onPointsUpdate(this.getUsername(),this.points,delta);
        return this.points;
    }

    /**
     * Method used to assign a ScoringToken to the player
     * @param token is the token to assign
     */
    public void addToken(ScoringToken token) {
        this.tokenAcquired.add(Objects.requireNonNull(token));
        /* notify the listener */
        this.playerListener.onTokenPointAcquired( this.username, new ArrayList<>(this.tokenAcquired));
    }

    /**
     * Method used to assign a PersonalGoalCard to the player
     * @param card is the card to assign
     */
    public void assignPersonalCard(PersonalGoalCard card) {
        this.personalCard = Objects.requireNonNull(card);
        playerListener.onPersonalGoalCardAssigned(this.username, card, this.getUsername());
    }

    /**
     * Override of the equals method, two Players are equals if they have the same username
     * @param o is an object
     * @return if two player have the same username
     */
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
