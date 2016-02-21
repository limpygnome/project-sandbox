package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.effect.EffectsManager;
import com.limpygnome.projectsandbox.server.entity.ai.ArtificialIntelligenceManager;
import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.entity.RespawnManager;
import com.limpygnome.projectsandbox.server.inventory.InventoryManager;
import com.limpygnome.projectsandbox.server.packet.PacketManager;
import com.limpygnome.projectsandbox.server.packet.PacketStatsManager;
import com.limpygnome.projectsandbox.server.player.ChatManager;
import com.limpygnome.projectsandbox.server.player.PlayerManager;
import com.limpygnome.projectsandbox.server.player.SessionManager;
import com.limpygnome.projectsandbox.server.threading.GameLogic;
import com.limpygnome.projectsandbox.server.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The main facade for the game, used to setup services and
 */
@Component
public class Controller
{
    public SocketEndpoint endpoint;
    public GameLogic logic;
    public Thread threadLogic;

    @Autowired
    public PacketManager packetManager;
    @Autowired
    public PacketStatsManager packetStatsManager;
    @Autowired
    public EntityManager entityManager;
    @Autowired
    public RespawnManager respawnManager;
    @Autowired
    public PlayerManager playerManager;
    @Autowired
    public ChatManager chatManager;
    @Autowired
    public InventoryManager inventoryManager;
    @Autowired
    public MapManager mapManager;
    @Autowired
    public EffectsManager effectsManager;
    @Autowired
    public ArtificialIntelligenceManager artificialIntelligenceManager;
    @Autowired
    public SessionManager sessionManager;

    public Controller()
    {
    }
    
    public void start()
    {
        try
        {
            // Invoke load methods on services
            inventoryManager.load();
            mapManager.load();

            // Setup logic thread
            logic = new GameLogic(this);
            threadLogic = new Thread(logic);
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
