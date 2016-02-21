package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.packet.PacketManager;
import com.limpygnome.projectsandbox.server.player.ChatService;
import com.limpygnome.projectsandbox.server.player.PlayerService;
import com.limpygnome.projectsandbox.server.player.SessionService;
import com.limpygnome.projectsandbox.server.service.LoadService;
import com.limpygnome.projectsandbox.server.threading.GameLogicThreadedService;
import com.limpygnome.projectsandbox.server.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import java.util.List;
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
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The endpoint used to accept and transfer data between clients.
     */
    public SocketEndpoint endpoint;

    /**
     * Thread used to execute periodic game logic cycles.
     */
    public Thread threadLogic;

    @Autowired
    private GameLogicThreadedService gameLogicService;

    @Autowired
    public PacketManager packetManager;
    @Autowired
    public PlayerService playerService;
    @Autowired
    public ChatService chatService;
    @Autowired
    public MapService mapService;
    @Autowired
    public SessionService sessionService;

    @Autowired
    private List<LoadService> loadServices;

    
    public void start()
    {
        try
        {
            // Invoke load methods on services requiring pre-logic loading...
            for (LoadService loadService : loadServices)
            {
                loadService.load();
            }

            // Setup logic thread
            threadLogic = new Thread(gameLogicService);
            threadLogic.start();

            // Start endpoint to receive clients...
            endpoint = new SocketEndpoint(this, 4857);
            endpoint.start();
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    
    public void stop()
    {
        // TODO: dispose managers etc...kill sockets / plys etc
        // TODO: consider sending packet to clients explaining shutdown reason?
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
