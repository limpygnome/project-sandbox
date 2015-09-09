package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Responsible for loading maps.
 */
public class MapManager
{
    private final static Logger LOG = LogManager.getLogger(MapManager.class);

    private Controller controller;

    /* A cache for storing active maps. */
    private HashMap<Short, Map> mapCache;

    public Map main;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.mapCache = new HashMap<>();
        this.main = null;
    }

    public synchronized void put(Map map)
    {
        mapCache.put(map.mapId, map);
    }
    
    public synchronized void load() throws Exception
    {
        FileSystemFile[] files = FileSystem.getResources(PathConstants.BASE_PACKAGE_MAPS);

        // Iterate and load each map file
        JSONObject obj;
        Map map;

        for(FileSystemFile file : files)
        {
            // Load map
            obj = JsonHelper.read(file.getInputStream());
            map = Map.load(controller, this, mapIdCounter, obj);
            
            // Map has loaded, now increment counter
            mapIdCounter++;
            
            // Add mapping
            mapCache.put(map.name, map);

            LOG.debug("Loaded map - {}", map);
        }
        
        // Set the main map file
        main = mapCache.get("main");
        
        if (main == null)
        {
            throw new IOException("Unable to find main map.");
        }
    }
    
}
