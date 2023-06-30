package it.polimi.ingsw;

import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.client.view.GUI.GUILaunch;
import it.polimi.ingsw.server.ServerMain;

/**
 * Launch file for the whole project, it instances either a client or a server depending
 * on the arguments given.
 */
public class Launch
{
    /**
     * Main method of the class
     * @param args arguments given (view readme file)
     */
    public static void main( String[] args )
    {
        if (args.length >= 2) {
            String[] newArgs = new String[args.length - 1];
            for( int j = 0; j < args.length - 1; j++){
                newArgs[j] = args[j + 1];
            }

            if (args[0].equals("--CLI")) {
                CliView.main(newArgs);
            } else if (args[0].equals("--GUI")) {
                GUILaunch.main(newArgs);
            } else {
                ServerMain.main(newArgs);
            }

        } else {
            ServerMain.main(args);
        }

    }
}
