package it.polimi.ingsw.client.view;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;


public class CliLanterna {

    private static int rows;
    private static int cols;
    public static void main(String[] args) throws IOException {

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

        TerminalSize terminalSize = new TerminalSize(210, 110);
        defaultTerminalFactory.setInitialTerminalSize(terminalSize);
        Terminal terminal = defaultTerminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);

        String title =
                        "███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗\n"+
                        "████╗ ████║╚██╗ ██╔╝    ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝\n"+
                        "██╔████╔██║ ╚████╔╝     ███████╗███████║█████╗  ██║     █████╗  ██║█████╗  \n"+
                        "██║╚██╔╝██║  ╚██╔╝      ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝  \n"+
                        "██║ ╚═╝ ██║   ██║       ███████║██║  ██║███████╗███████╗██║     ██║███████╗\n"+
                        "╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝\n";

        rows = terminal.getTerminalSize().getRows() - 2;
        cols = terminal.getTerminalSize().getColumns() - 5;
        int startRow = 1;
        int startCol = 5;
        System.out.println(rows+" "+cols);
        TextGraphics tg = screen.newTextGraphics();
        screen.startScreen();
        //screen.doResizeIfNecessary();

        tg.putString(new TerminalPosition(startCol,startRow),"┌");
        tg.drawLine(new TerminalPosition(startCol+1,startRow),new TerminalPosition(cols-1,startRow),'─');
        tg.putString(new TerminalPosition(cols,startRow),"┐");
        tg.putString(new TerminalPosition(startCol,rows),"└");
        tg.putString(new TerminalPosition(cols,rows),"┘");

        tg.drawLine(new TerminalPosition(startCol+1,rows),new TerminalPosition(cols-1,rows),'─');
        tg.drawLine(new TerminalPosition(startCol,startRow+1),new TerminalPosition(startCol,rows-1),'│');
        tg.drawLine(new TerminalPosition(cols,startRow+1),new TerminalPosition(cols,rows-1),'│');

        tg.setForegroundColor(TextColor.ANSI.YELLOW);

        int i = 0;
        for( String line : title.split("\n")){
            tg.putString(new TerminalPosition(startCol+5, startRow+5+i),line);
            i++;
        }
        screen.refresh();
        screen.readInput();
        tg.drawRectangle(new TerminalPosition(3,3), new TerminalSize(10,4),'*');
        screen.refresh();

    }



}

