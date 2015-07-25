package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.effect.EffectsManager;
import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.entity.RespawnManager;
import com.limpygnome.projectsandbox.server.inventory.InventoryManager;
import com.limpygnome.projectsandbox.server.packet.PacketManager;
import com.limpygnome.projectsandbox.server.player.ChatManager;
import com.limpygnome.projectsandbox.server.player.PlayerManager;
import com.limpygnome.projectsandbox.server.threading.GameLogic;
import com.limpygnome.projectsandbox.server.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.server.world.MapManager;

/**
 *
 * @author limpygnome
 */
public class Controller
{
    public SocketEndpoint endpoint;
    public GameLogic logic;
    public Thread threadLogic;

    public PacketManager packetManager;
    public EntityManager entityManager;
    public RespawnManager respawnManager;
    public PlayerManager playerManager;
    public ChatManager chatManager;
    public InventoryManager inventoryManager;
    public MapManager mapManager;
    public EffectsManager effectsManager;

    public Controller()
    {
    }
    
    public void start()
    {
        try
        {
            // Setup managers
            packetManager = new PacketManager(this);

            entityManager = new EntityManager(this);

            respawnManager = new RespawnManager(this);

            effectsManager = new EffectsManager(this);
            
            inventoryManager = new InventoryManager();
            inventoryManager.load();
            
            playerManager = new PlayerManager(this);

            chatManager = new ChatManager();

            mapManager = new MapManager(this);
            mapManager.load();
            
            endpoint = new SocketEndpoint(this, 4857);
            endpoint.start();
            
            // Setup logic thread
            logic = new GameLogic(this);
            threadLogic = new Thread(logic);
            threadLogic.start();
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
