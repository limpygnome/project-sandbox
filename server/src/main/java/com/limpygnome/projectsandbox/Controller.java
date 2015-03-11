package com.limpygnome.projectsandbox;

import com.limpygnome.projectsandbox.textures.TextureManager;
import com.limpygnome.projectsandbox.ents.EntityManager;
import com.limpygnome.projectsandbox.ents.PlayerManager;
import com.limpygnome.projectsandbox.threading.GameLogic;
import com.limpygnome.projectsandbox.world.MapManager;

/**
 *
 * @author limpygnome
 */
public class Controller
{
    public Endpoint endpoint;
    public GameLogic logic;
    public Thread threadLogic;
    
    public EntityManager entityManager;
    public PlayerManager playerManager;
    public TextureManager textureManager;
    public MapManager mapManager;
    
    public Controller()
    {
    }
    
    public void start()
    {
        try
        {
            // Clear entities
            entityManager = new EntityManager(this);
            
            // Setup manager for players
            playerManager = new PlayerManager(this);
            
            // Setup texture manager
            textureManager = new TextureManager();
            textureManager.load();
            
            // Setup map manager
            mapManager = new MapManager(this);
            mapManager.load();
            
            // Setup endpoint
            endpoint = new Endpoint(this, 4857);
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
