package com.projectsandbox.components.server.map.editor.service;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.respawn.RespawnMapData;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.map.editor.entity.SpawnMarker;
import com.projectsandbox.components.server.service.EventServerPostStartup;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Faction;
import com.projectsandbox.components.server.world.spawn.Spawn;
import java.util.Set;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * A service to create spawn markers for all the markers, on post-startup of the server, for a map.
 *
 * TODO: make map loaded event instead, since this will be dynamic in the future...
 */
@Service
public class SpawnMarkerService implements EventServerPostStartup
{
    private Map<Spawn, SpawnMarker> spawnMarkers;

    public SpawnMarkerService()
    {
        spawnMarkers = new HashMap<>();
    }

    @Override
    public void eventServerPostStartup(Controller controller)
    {
        reload(controller);
    }

    /**
     * When invoked, re-creates all spawn markers.
     */
    public synchronized void reload(Controller controller)
    {
        Map<String, WorldMap> maps = controller.mapService.getAll();

        RespawnMapData respawnMapData;
        Map<Short, Faction> factionSpawnsMap;
        Set<Spawn> spawns;

        // Iterate each map
        for (WorldMap map : maps.values())
        {
            respawnMapData = map.getRespawnMapData();
            factionSpawnsMap = respawnMapData.getFactions();

            // Iterate spawns for each faction
            for (Faction faction : factionSpawnsMap.values())
            {
                spawns = faction.getSpawns();

                for (Spawn spawn : spawns)
                {
                    createMarker(controller, map, spawn);
                }
            }
        }
    }

    private void createMarker(Controller controller, WorldMap map, Spawn spawn)
    {
        // Create entity and add for respawn
        SpawnMarker spawnMarker = new SpawnMarker();

        PositionPendingRespawn respawn = new PositionPendingRespawn(controller, map, spawnMarker, spawn, 0);
        controller.respawnManager.respawn(respawn);

        // Add marker to collection for tracking
        spawnMarkers.put(spawn, spawnMarker);
    }

}
