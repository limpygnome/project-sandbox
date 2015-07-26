package com.limpygnome.projectsandbox.server.threading;

import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;

/**
 *
 * @author limpygnome
 */
public class GameLogic implements Runnable
{
    /**
     * The time between logic cycles, which dictates the speed at which the world executes.
     */
    public static final int TICK_RATE_MS = 60;

    private static Boolean instantiated = false;
    
    private Controller controller;
    
    public GameLogic(Controller controller)
    {
        // Ensure only a single instance of this class is ever started
        synchronized (instantiated)
        {
            if (instantiated)
            {
                throw new RuntimeException("Game logic thread already running, cannot be more than once instance");
            }

            instantiated = true;
        }

        this.controller = controller;
    }
    
    public void run()
    {
        // Set name of thread for logs
        Thread.currentThread().setName("Game Logic");

        // Run the world!
        try
        {
            long timeStart, timeEnd, timeNext;

            while(!Thread.interrupted())
            {
                timeStart = System.currentTimeMillis();
                
                try
                {
                    // Run logic for entities
                    controller.entityManager.logic();

                    // Run logic for respawn manager
                    controller.respawnManager.logic();

                    // Run logic for effects
                    controller.effectsManager.logic();

                    // Run logic for players
                    controller.playerManager.logic();

                    // Run logic for session management
                    controller.sessionManager.logic();
                }
                catch(IOException ex)
                {
                    ex.printStackTrace(System.err);
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
                    System.err.println("Warning: game logic cycle took " + (timeEnd - timeStart) + " ms or longer.");
                }
            }
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
}
