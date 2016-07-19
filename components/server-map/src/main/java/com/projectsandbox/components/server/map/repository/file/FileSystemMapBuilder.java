package com.projectsandbox.components.server.map.repository.file;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Implemented by different types of maps to construct an instance from JSON map data.
 *
 * Implementations should be thread-safe.
 */
public interface FileSystemMapBuilder
{

    /**
     * The name of the builder. Also used for when loading a map and deciding which builder should be used.
     *
     * @return builder name
     */
    String getBuilderName();

    /**
     * Creates a new instance of {@link WorldMap}.
     *
     * @param controller
     * @param mapService
     * @param mapId
     * @return
     */
    WorldMap createMapInstance(Controller controller, MapService mapService, short mapId);

    /**
     * Builds an instance from the provided map data.
     *
     * @param controller
     * @param mapService
     * @param mapData
     * @return
     */
    WorldMap build(Controller controller, MapService mapService, JSONObject mapData) throws IOException;

}
