package com.projectsandbox.components.server.map.editor.service;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.respawn.RespawnMapData;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.map.editor.entity.SpawnMarker;
import com.projectsandbox.components.server.service.EventServerPostStartup;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Faction;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A service to create spawn marker entities in place of spawn points.
 *
 * Currently works by adding markers on startup i.e. post map load
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
        List<Spawn> spawns;

        // Iterate each map
        for (WorldMap map : maps.values())
        {
            respawnMapData = map.getRespawnMapData();
            factionSpawnsMap = respawnMapData.getFactionSpawns();

            // Iterate spawns for each faction
            for (Faction faction : factionSpawnsMap.values())
            {
                spawns = faction.getSpawns();

                for (Spawn spawn : spawns)
                {
                    create(controller, map, faction, spawn);
                }
            }
        }
    }

    /**
     * Should be invoked when a spawn is created.
     */
    public synchronized void create(Controller controller, WorldMap map, Faction faction, Spawn spawn)
    {
        // Create entity and add for respawn
        SpawnMarker spawnMarker = new SpawnMarker();
        PositionPendingRespawn respawn = new PositionPendingRespawn(controller, map, spawnMarker, spawn, 0);
        controller.respawnManager.respawn(respawn);

        // Add marker to collection for tracking
        spawnMarkers.put(spawn, spawnMarker);
    }

    /**
     * Should be invoked when a spawn is removed.
     */
    public synchronized void remove(Controller controller, Spawn spawn)
    {
        SpawnMarker spawnMarker = spawnMarkers.remove(spawn);

        if (spawnMarker != null)
        {
            // Remove from world
            controller.entityManager.remove(spawnMarker);
        }
    }

}
