package it.polimi.ingsw.client.view;


import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.cards.common.CommonCardType;
import it.polimi.ingsw.server.model.cards.common.MarioPyramid;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CLITest {


    /**
     * test for the title print
     */
    @Test
    @DisplayName("Test the title print of the CLI")
    void testTitle(){
        CliView cli = new CliView(ConnectionType.RMI);
        cli.printAsciiArtTitle();
    }


    @Test
    @DisplayName("Test the print of the bookshelfs and the personal goal card")
    void testBookshelfPrint() throws InvalidCoordinatesException {
        CliView cli = new CliView(ConnectionType.RMI);
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.GAME);
        testPattern.put(new Coordinates(5,4), ItemTile.CAT);
        testPattern.put(new Coordinates(3,3), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,0), ItemTile.PLANT);
        testPattern.put(new Coordinates(0,4), ItemTile.GAME);

        cli.drawBookShelf(testPattern, "Your Bookshelf", 0);
        cli.drawBookShelf(testPattern,"Piero Pelu", 1);
        cli.drawBookShelf(testPattern,"Mario", 2);
        cli.drawBookShelf(testPattern,"gino", 3);

        Map<Integer,Integer> pointReference = new HashMap<>();

        pointReference.put(1,1);
        pointReference.put(2,2);
        pointReference.put(3,4);
        pointReference.put(4,6);
        pointReference.put(5,9);
        pointReference.put(6,12);

      //  cli.drawPersonalCard(testPattern,pointReference);

        Map<Coordinates, ItemTile> testBoardPattern = new HashMap<>();
        testBoardPattern.put(new Coordinates(7,3), ItemTile.GAME);
        testBoardPattern.put(new Coordinates(7,4), ItemTile.CAT);

        testBoardPattern.put(new Coordinates(6,3), ItemTile.TROPHY);
        testBoardPattern.put(new Coordinates(6,4), ItemTile.TROPHY);
        testBoardPattern.put(new Coordinates(6,5), ItemTile.EMPTY);

        testBoardPattern.put(new Coordinates(5,2), ItemTile.PLANT);
        testBoardPattern.put(new Coordinates(5,3), ItemTile.GAME);
        testBoardPattern.put(new Coordinates(5,4), ItemTile.GAME);
        testBoardPattern.put(new Coordinates(5,5), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(5,6), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(5,7), ItemTile.EMPTY);

        testBoardPattern.put(new Coordinates(4,1), ItemTile.PLANT);
        testBoardPattern.put(new Coordinates(4,2), ItemTile.GAME);
        testBoardPattern.put(new Coordinates(4,3), ItemTile.GAME);
        testBoardPattern.put(new Coordinates(4,4), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(4,5), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(4,6), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(4,7), ItemTile.EMPTY);

        testBoardPattern.put(new Coordinates(3,1), ItemTile.CAT);
        testBoardPattern.put(new Coordinates(3,2), ItemTile.CAT);
        testBoardPattern.put(new Coordinates(3,3), ItemTile.PLANT);
        testBoardPattern.put(new Coordinates(3,4), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(3,5), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(3,6), ItemTile.PLANT);

        testBoardPattern.put(new Coordinates(2,3), ItemTile.TROPHY);
        testBoardPattern.put(new Coordinates(2,4), ItemTile.TROPHY);
        testBoardPattern.put(new Coordinates(2,5), ItemTile.EMPTY);

        testBoardPattern.put(new Coordinates(1,4), ItemTile.EMPTY);
        testBoardPattern.put(new Coordinates(1,5), ItemTile.EMPTY);

        cli.drawLivingRoom(testBoardPattern);

        Map<Integer, Integer> testPointsReference = new HashMap<>();
        testPointsReference.put(1,1);
        testPointsReference.put(2,2);
        testPointsReference.put(4,6);
        testPointsReference.put(6,8);

        cli.drawPersonalCard(testPattern, testPointsReference);

        String description1 = "Two columns each formed by 6 different types of tiles.";
        String description2 = "Two lines each formed by 5 different types of tiles. One line can show the same or a different combination of the other line.";
        RemoteCommonGoalCard card1;
        RemoteCommonGoalCard card2;
        try {
            card1 = new MarioPyramid(3, description1, CommonCardType.CORNERS);
            card2 = new MarioPyramid(3, description2, CommonCardType.CORNERS);
        } catch (PlayersNumberOutOfRange e) {
            throw new RuntimeException(e);
        }

        ArrayList<RemoteCommonGoalCard> commonCards = new ArrayList<>();
        commonCards.add(card1);
        commonCards.add(card2);

        try {
            cli.drawCommonCards(commonCards);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> chat = new ArrayList<>();
        chat.add(0, "<CHAT> aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        chat.add(0, "<TEST> aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        chat.add(0, "<AJEJE> aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        chat.add(0, "<ADWDADWA> aaaa");
        chat.add(0, "<CEO> aaaaaaaaaaaaaaaaaaaaaaaaaaa");
        chat.add(0, "<TANAN> aaaaaaaaaaaauhukhukuhaa");

        cli.drawChat(chat);

        LinkedHashMap<String, Integer> scoreBoard = new LinkedHashMap<>();
        scoreBoard.put("Player1", 15);
        scoreBoard.put("Player2", 10);
        scoreBoard.put("Player3", 5);
        scoreBoard.put("Player4", 0);

        cli.drawLeaderboard(scoreBoard);
        cli.postNotification("NOTIFICA!","notifica inviata dal server che descrive un qualcosa da fare, una azione o un cambio turno!");

    }
}
