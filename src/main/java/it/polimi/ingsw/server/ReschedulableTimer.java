package it.polimi.ingsw.server;
import java.util.Timer;
import java.util.TimerTask;

import static it.polimi.ingsw.server.ServerMain.logger;

public class ReschedulableTimer extends Timer
{
    private Runnable  task;
    private TimerTask timerTask;

    /**
     *
     * @return true if this timer has a task associated, false otherwise
     */
    public boolean isScheduled(){
        if(task != null) return true;
        return false;
    }

    /**
     * This method schedule a task to be performed after a delay
     * @param runnable action to be performed
     * @param delay time between the schedule and the execution
     */
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

    /**
     * This method reschedule the task to after a delay
     * @param delay time between the rescheduling and the task execution
     */
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