package com.limpygnome.projectsandbox.server;

import com.limpygnome.projectsandbox.server.effects.EffectsManager;
import com.limpygnome.projectsandbox.server.ents.EntityManager;
import com.limpygnome.projectsandbox.server.inventory.InventoryManager;
import com.limpygnome.projectsandbox.server.packets.PacketManager;
import com.limpygnome.projectsandbox.server.players.PlayerManager;
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
    public PlayerManager playerManager;
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
            // Setup manager for packets
            packetManager = new PacketManager(this);

            // Setup manager for entities
            entityManager = new EntityManager(this);

            // Setup manager for effects
            effectsManager = new EffectsManager(this);
            
            // Setup manager for inventories
            inventoryManager = new InventoryManager();
            inventoryManager.load();
            
            // Setup manager for players
            playerManager = new PlayerManager(this);

            // Setup map manager
            mapManager = new MapManager(this);
            mapManager.load();
            
            // Setup endpoint
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
}
