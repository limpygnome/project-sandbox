package com.limpygnome.projectsandbox.server.threading;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.service.LogicService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Thread for running game logic.
 */
@Service
public class GameLogicThreadedService implements Runnable
{
    private final static Logger LOG = LogManager.getLogger(GameLogicThreadedService.class);

    /**
     * The time between logic cycles, which dictates the speed at which the world executes.
     */
    public static final int TICK_RATE_MS = 60;

    @Autowired
    private Controller controller;
    @Autowired
    private List<LogicService> logicServices;

    public void run()
    {
        // Set name of thread for logs
        Thread.currentThread().setName("Game Logic");

        // Run the world!
        try
        {
            long timeStart, timeEnd, timeNext;

            while (!Thread.interrupted())
            {
                timeStart = System.currentTimeMillis();

                // Execute logic for each service
                for (LogicService logicService : logicServices)
                {
                    logicService.logic();
                }
                
                // Sleep for another cycle
                timeEnd = System.currentTimeMillis();
                timeNext = TICK_RATE_MS - (timeEnd - timeStart);
                
                if(timeNext > 0)
                {
                    Thread.sleep(timeNext);
                }
                else
                {
                    LOG.warn("game logic cycle slow - {} ms", (timeEnd - timeStart));
                }
            }
        }
        catch(InterruptedException ex)
        {
            LOG.error("Logic cycle failure, thread interrupted", ex);
        }
    }

}
