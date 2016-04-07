package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.entity.factory.PlayerEntityService;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;
import com.limpygnome.projectsandbox.server.player.ChatService;
import com.limpygnome.projectsandbox.server.player.PlayerService;
import com.limpygnome.projectsandbox.server.player.SessionService;
import com.limpygnome.projectsandbox.server.service.EventServerPostStartup;
import com.limpygnome.projectsandbox.server.service.EventServerShutdown;
import com.limpygnome.projectsandbox.server.service.EventServerPreStartup;
import com.limpygnome.projectsandbox.server.threading.GameLogicThreadedService;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The main facade for the game, used to setup services and
 */
@Component
public class Controller
{
    private final static Logger LOG = LogManager.getLogger(PlayerService.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Thread used to execute periodic game logic cycles.
     */
    public Thread threadLogic;

    @Autowired
    private GameLogicThreadedService gameLogicService;

    @Autowired
    public PacketService packetService;
    @Autowired
    public PlayerService playerService;
    @Autowired
    public PlayerEntityService playerEntityService;
    @Autowired
    public ChatService chatService;
    @Autowired
    public SessionService sessionService;

    @Autowired
    private List<EventServerPreStartup> eventServerPreStartups;
    @Autowired
    private List<EventServerPostStartup> eventServerPostStartups;
    @Autowired
    private List<EventServerShutdown> eventServerShutdowns;

    
    public void startAndJoin()
    {
        try
        {
            // Invoke pre-startup events on services
            for (EventServerPreStartup eventServerPreStartup : eventServerPreStartups)
            {
                eventServerPreStartup.eventServerStartup(this);
            }

            // Setup logic thread
            threadLogic = new Thread(gameLogicService);
            threadLogic.start();

            // Invoke post-startup events on services
            for (EventServerPostStartup eventServerPostStartup : eventServerPostStartups)
            {
                eventServerPostStartup.eventServerPostStartup(this);
            }

            // Join logic thread
            threadLogic.join();

            // Since we've left the logic thread, shutdown...
            stop();
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    
    public void stop()
    {
        LOG.info("Server shutting down...");

        // TODO: dispose managers etc...kill sockets / plys etc
        // TODO: consider sending packet to clients explaining shutdown reason?

        // Invoke shutdown events
        for (EventServerShutdown eventServerShutdown : eventServerShutdowns)
        {
            eventServerShutdown.eventServerShutdown(this);
        }

        LOG.info("Server shutdown successfully");
    }

    public long gameTime()
    {
        // TODO: we should change this to be pausable etc?
        return System.currentTimeMillis();
    }

    /**
     * Manually injects any dependencies into the provided object.
     *
     * @param o the object to receive dependency injection
     */
    public void inject(Object o)
    {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        factory.autowireBean(o);
        factory.initializeBean(o, "bean");
    }

}
