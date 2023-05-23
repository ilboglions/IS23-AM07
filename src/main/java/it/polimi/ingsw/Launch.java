package it.polimi.ingsw;

import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.client.view.GUI.GUILaunch;
import it.polimi.ingsw.server.ServerMain;

/**
 * Hello world!
 *
 */
public class Launch
{
    public static void main( String[] args )
    {

        if (args.length == 3) {
            String[] newArgs = new String[2];
            newArgs[0] = args[0];
            newArgs[1] = args[2];
            if (args[1].equals("--CLI")) {
                CliView.main(newArgs);
            } else if (args[1].equals("--GUI")) {
                GUILaunch.main(newArgs);
            } else {
                ServerMain.main(newArgs);
            }

        } else {
            ServerMain.main(args);
        }

    }
}
