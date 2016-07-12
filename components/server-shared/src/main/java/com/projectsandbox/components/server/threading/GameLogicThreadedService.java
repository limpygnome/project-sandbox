package com.projectsandbox.components.server.threading;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.service.EventLogicCycleService;
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
    private List<EventLogicCycleService> eventLogicCycleServices;

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
                for (EventLogicCycleService eventLogicCycleService : eventLogicCycleServices)
                {
                    try
                    {
                        eventLogicCycleService.logic();
                    }
                    catch (Exception e)
                    {
                        if (e instanceof InterruptedException)
                        {
                            throw e;
                        }

                        LOG.error("Failed to execute logic for service", e);
                    }
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
        catch (InterruptedException ex)
        {
            LOG.error("Logic cycle thread interrupted", ex);
        }
    }

}
