package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.effect.EffectsManager;
import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.entity.RespawnManager;
import com.limpygnome.projectsandbox.server.entity.ai.ArtificialIntelligenceManager;
import com.limpygnome.projectsandbox.server.inventory.InventoryManager;
import com.limpygnome.projectsandbox.server.packet.PacketManager;
import com.limpygnome.projectsandbox.server.player.ChatManager;
import com.limpygnome.projectsandbox.server.player.PlayerManager;
import com.limpygnome.projectsandbox.server.player.SessionManager;
import com.limpygnome.projectsandbox.server.service.LoadService;
import com.limpygnome.projectsandbox.server.threading.GameLogicThreadedService;
import com.limpygnome.projectsandbox.server.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The main facade for the game, used to setup services and
 */
@Component
public class Controller
{
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
    public EntityManager entityManager;
    @Autowired
    public RespawnManager respawnManager;
    @Autowired
    public PlayerManager playerManager;
    @Autowired
    public ChatManager chatManager;
    @Autowired
    public MapManager mapManager;
    @Autowired
    public EffectsManager effectsManager;
    @Autowired
    public ArtificialIntelligenceManager artificialIntelligenceManager;
    @Autowired
    public SessionManager sessionManager;

    @Autowired
    private List<LoadService> loadServices;

    public Controller()
    {
    }
    
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

}
