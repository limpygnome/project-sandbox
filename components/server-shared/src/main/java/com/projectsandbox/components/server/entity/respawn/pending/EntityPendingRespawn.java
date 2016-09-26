package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.FactionSpawns;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Respawns an entity using its properties:
 *
 * - First looks to see if a fixed spawn is set for the entity.
 * - Checks if the entity belongs to a faction, and if that faction has spawns for the map.
 */
public class EntityPendingRespawn extends PendingRespawn
{
    private final static Logger LOG = LogManager.getLogger(EntityPendingRespawn.class);

    public EntityPendingRespawn(Controller controller, WorldMap map, Entity entity)
    {
        super(controller, map, entity, 0);
    }

    public EntityPendingRespawn(Controller controller, WorldMap map, Entity entity, long respawnDelay)
    {
        super(controller, map, entity, respawnDelay);
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        // Check ent for its own custom spawn
        if (entity.spawn != null)
        {
            return entity.spawn;
        }

        // Attempt to spawn using spawns for faction
        FactionSpawns factionSpawns = map.getRespawnMapData().factionSpawnsGet(entity.faction);

        if (factionSpawns == null)
        {
            LOG.warn("Cannot find faction spawns for entity - faction id: {}, factions: {}, entity id: {}",
                    entity.faction, factionSpawns, entity.id);

            return null;
        }
        // Use next available spawn, if spawns available
        else if (factionSpawns.hasSpawns())
        {
            return factionSpawns.getNextSpawn();
        }
        else
        {
            LOG.warn("No spawns available for factionSpawns - id: {}, factionSpawns: {}", entity.id, entity.faction);

            return null;
        }
    }

}
