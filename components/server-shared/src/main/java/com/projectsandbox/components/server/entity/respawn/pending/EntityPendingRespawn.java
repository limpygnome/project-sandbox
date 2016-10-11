package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Faction;
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

    public EntityPendingRespawn(Controller controller, WorldMap map, Entity entity, long respawnDelay, boolean loadedFromSession)
    {
        super(controller, map, entity, respawnDelay, loadedFromSession);
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
        Faction faction = map.getRespawnMapData().getFaction(entity.factionId);

        if (faction == null)
        {
            LOG.warn("Cannot find faction spawns for entity - faction id: {}, faction: {}, entity id: {}",
                    entity.factionId, faction, entity.id);

            return null;
        }
        // Use next available spawn, if spawns available
        else if (faction.hasSpawns())
        {
            return faction.getNextSpawn();
        }
        else
        {
            LOG.warn("No spawns available for faction - id: {}, faction: {}", entity.id, entity.factionId);

            return null;
        }
    }

}
