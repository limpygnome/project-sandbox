package com.limpygnome.projectsandbox.server.world;

import com.limpygnome.projectsandbox.server.constants.PathConstants;
import com.limpygnome.projectsandbox.server.utils.FileSystem;
import com.limpygnome.projectsandbox.server.utils.FileSystemFile;
import com.limpygnome.projectsandbox.server.utils.JsonHelper;
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

    public HashMap<String, Map> maps;
    private short mapIdCounter;
    
    public Map main;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.maps = new HashMap<>();
        this.mapIdCounter = 0;
        this.main = null;
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
            maps.put(map.name, map);

            LOG.debug("Loaded map - {}", map);
        }
        
        // Set the main map file
        main = maps.get("main");
        
        if (main == null)
        {
            throw new IOException("Unable to find main map.");
        }
    }
    
}
