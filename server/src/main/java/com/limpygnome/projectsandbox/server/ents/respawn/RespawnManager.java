package com.limpygnome.projectsandbox.server.ents.respawn;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.EntityManager;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A layer above {@link EntityManager} for respawning an entity with additional params.
 */
public class RespawnManager
{
    private final static Logger LOG = LogManager.getLogger(RespawnManager.class);

    private Controller controller;
    private LinkedList<RespawnProperties> respawnPropertiesList;

    public RespawnManager(Controller controller)
    {
        this.controller = controller;
        this.respawnPropertiesList = new LinkedList<>();
    }

    public synchronized void respawn(RespawnProperties respawnProperties)
    {
        // Check if already active, so we can remove it
        if (respawnProperties.alreadyActive)
        {
            respawnProperties.entity.remove();
        }

        // Add at suitable index based on time to respawn
        Iterator<RespawnProperties> iterator = respawnPropertiesList.iterator();
        RespawnProperties respawnPropertiesItem;
        int index = 0;

        while (iterator.hasNext())
        {
            respawnPropertiesItem = iterator.next();

            if (respawnPropertiesItem.gameTimeRespawn > respawnProperties.gameTimeRespawn)
            {
                // We have found the index to insert our item
                break;
            }
            else
            {
                index++;
            }
        }

        respawnPropertiesList.add(index, respawnProperties);

        LOG.debug("Entity added for respawn - ent id: {}, index: {}", respawnProperties.entity.id, index);
    }

    public synchronized void logic()
    {
        // Check if next entity can respawn yet
        RespawnProperties respawnProperties;
        Iterator<RespawnProperties> iterator = respawnPropertiesList.iterator();
        Entity entity;

        while (iterator.hasNext() && (respawnProperties = iterator.next()).gameTimeRespawn <= controller.gameTime())
        {
            entity = respawnProperties.entity;

            // Spawn entity
            if (controller.mapManager.main.spawn(entity))
            {
                // Add to entity manager to re-add to world
                if (controller.entityManager.add(entity))
                {
                    // Remove from our spawn manager
                    iterator.remove();
                }
                else
                {
                    LOG.warn("Unable to add entity to entity manager during respawn - ent id: " + entity.id);
                }
            }
        }
    }

}
