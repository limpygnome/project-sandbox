package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.limpygnome.projectsandbox.server.world.map.data.MapBuilder;
import com.limpygnome.projectsandbox.server.world.map.repository.FileSystemMapRepository;
import com.limpygnome.projectsandbox.server.world.map.repository.MapRepository;
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

    /* The repository used for fetching maps. */
    private MapRepository mapRepository;

    /* The implementation used for building maps.  */
    private MapBuilder mapBuilder;

    /* A cache for storing either common or active maps. */
    private Map<UUID, WorldMap> mapCache;

    /* The main/lobby map. */
    public WorldMap main;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.mapCache = new HashMap<>();
        this.main = null;
    }

    public synchronized void put(WorldMap map)
    {
        mapCache.put(map.mapId, map);
    }
    
    public synchronized void load() throws Exception
    {
        MapRepository mapRepository = new FileSystemMapRepository();
        mapCache = new HashMap<>(mapRepository.fetchPublicMaps(mapBuilder));
    }

    public synchronized void loadOld() throws Exception
    {

    }
    
}
