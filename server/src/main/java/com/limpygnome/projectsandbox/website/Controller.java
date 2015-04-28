package com.limpygnome.projectsandbox.website;

import com.limpygnome.projectsandbox.website.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.website.ents.EntityManager;
import com.limpygnome.projectsandbox.website.inventory.InventoryManager;
import com.limpygnome.projectsandbox.website.players.PlayerManager;
import com.limpygnome.projectsandbox.website.threading.GameLogic;
import com.limpygnome.projectsandbox.website.ents.EntityManager;
import com.limpygnome.projectsandbox.website.inventory.InventoryManager;
import com.limpygnome.projectsandbox.website.players.PlayerManager;
import com.limpygnome.projectsandbox.website.threading.GameLogic;
import com.limpygnome.projectsandbox.website.threading.SocketEndpoint;
import com.limpygnome.projectsandbox.website.world.MapManager;
import com.limpygnome.projectsandbox.website.world.MapManager;

/**
 *
 * @author limpygnome
 */
public class Controller
{
    public SocketEndpoint endpoint;
    public GameLogic logic;
    public Thread threadLogic;
    
    public EntityManager entityManager;
    public PlayerManager playerManager;
    public InventoryManager inventoryManager;
    public MapManager mapManager;
    
    public Controller()
    {
    }
    
    public void start()
    {
        try
        {
            // Setup manager for entities
            entityManager = new EntityManager(this);
            
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
    }
}
