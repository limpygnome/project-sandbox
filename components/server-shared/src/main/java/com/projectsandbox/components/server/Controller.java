package com.projectsandbox.components.server;

import com.projectsandbox.components.server.effect.EffectsManager;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.PlayerEntityService;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.network.packet.PacketService;
import com.projectsandbox.components.server.player.ChatService;
import com.projectsandbox.components.server.player.PlayerService;
import com.projectsandbox.components.server.player.SessionService;
import com.projectsandbox.components.server.service.EventServerPostStartup;
import com.projectsandbox.components.server.service.EventServerShutdown;
import com.projectsandbox.components.server.service.EventServerPreStartup;
import com.projectsandbox.components.server.threading.GameLogicThreadedService;
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
    private final static Logger LOG = LogManager.getLogger(Controller.class);

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
    public SessionService sessionService;
    @Autowired
    public ChatService chatService;

    @Autowired
    public EntityManager entityManager;
    @Autowired
    public PlayerEntityService playerEntityService;
    @Autowired
    public RespawnManager respawnManager;
    @Autowired
    public EffectsManager effectsManager;

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
            LOG.info("starting instance...");

            // Invoke pre-startup events on services
            for (EventServerPreStartup eventServerPreStartup : eventServerPreStartups)
            {
                eventServerPreStartup.eventServerPreStartup(this);
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
            LOG.info("joining logic thread...");
            threadLogic.join();

            // Since we've left the logic thread, shutdown...
            LOG.info("stopping instance...");
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
