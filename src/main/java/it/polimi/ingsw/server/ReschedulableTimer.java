package it.polimi.ingsw.server;
import java.util.Timer;
import java.util.TimerTask;

import static it.polimi.ingsw.server.ServerMain.logger;

public class ReschedulableTimer extends Timer
{
    private Runnable  task;
    private TimerTask timerTask;

    public boolean isScheduled(){
        if(task != null) return true;
        return false;
    }
    public void schedule(Runnable runnable, long delay)
    {
        task = runnable;
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }

    public void reschedule(long delay)
    {

        timerTask.cancel();
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }
}