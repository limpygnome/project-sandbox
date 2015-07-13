package com.limpygnome.projectsandbox.server.ents.respawn.pending;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.world.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.Spawn;
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

    public EntityPendingRespawn(Entity entity)
    {
        super(entity, 0);
    }

    public EntityPendingRespawn(Entity entity, long gameTimeRespawn)
    {
        super(entity, gameTimeRespawn);
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
        FactionSpawns factionSpawns = controller.respawnManager.factionSpawnsGet(controller.mapManager.main.mapId, entity.faction);

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
