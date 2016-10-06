package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.respawn.SpawnParserHelper;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.util.IdCounterProvider;
import com.projectsandbox.components.server.util.counters.IdCounterConsumer;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.mapdata.MapData;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limpygnome on 21/07/16.
 */
@Component
@Scope(value = "prototype")
public class EntityMapData implements IdCounterConsumer, MapData
{
    private final static Logger LOG = LogManager.getLogger(EntityMapData.class);

    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;
    @Autowired
    private RespawnManager respawnManager;
    @Autowired
    private SpawnParserHelper spawnParserHelper;

    /* Used for efficient collision detection and network updates. */
    protected transient QuadTree quadTree;

    /* A map of entity id -> entity. */
    protected Map<Short, Entity> entities;

    /* Counter for producing entity identifiers. */
    private IdCounterProvider idCounterProvider;

    public EntityMapData()
    {
        this.idCounterProvider = new IdCounterProvider(this);
        this.quadTree = null;
        this.entities = null;
    }

    @Override
    public synchronized void serialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
        // Create new root for entities
        JSONArray entities = new JSONArray();

        Entity entity;
        JSONObject entityData;

        for (Map.Entry<Short, Entity> kv : this.entities.entrySet())
        {
            entity = kv.getValue();

            if (entity.mapSpawned)
            {
                // Serialize entity
                entityData = serializeEntity(controller, map, entity);

                // Add to available entities
                entities.add(entityData);
            }
        }

        // Attach entities to root
        root.put("entities", entities);
    }

    private JSONObject serializeEntity(Controller controller, WorldMap map, Entity entity)
    {
        JSONObject entityData = new JSONObject();

        // Fetch entity type name, more human readable
        String typeName = entityTypeMappingStoreService.getTypeName(entity);

        // Serialize general attributes
        entityData.put("typeName", typeName);
        entityData.put("faction", entity.faction);

        // Serialize spawn data
        Spawn spawn = entity.spawn;
        if (spawn != null)
        {
            entityData.put("spawn", spawnParserHelper.serialize(spawn));
        }

        // Serialize custom properties/data for entity instance
        if (entity instanceof EntityMapDataSerializer)
        {
            EntityMapDataSerializer serializer = (EntityMapDataSerializer) entity;
            serializer.serialize(controller, map, entityData);
        }

        return entityData;
    }

    @Override
    public synchronized void deserialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
        // Reset state
        reset(map);

        // Reload entities
        JSONArray rawEntities = (JSONArray) root.get("entities");
        JSONObject rawEntity;

        for (Object rawEntityObject : rawEntities)
        {
            rawEntity = (JSONObject) rawEntityObject;
            deserializeEntity(controller, map, rawEntity);
        }
    }

    protected void deserializeEntity(Controller controller, WorldMap map, JSONObject entityData) throws IOException
    {
        // Create instance
        Entity entity;

        if (entityData.containsKey("typeName"))
        {
            String typeName = (String) entityData.get("typeName");
            entity = entityTypeMappingStoreService.createByTypeName(typeName);
        }
        else if (entityData.containsKey("typeId"))
        {
            short typeId = (short) (long) entityData.get("typeId");
            entity = entityTypeMappingStoreService.createByTypeId(typeId);
        }
        else
        {
            throw new RuntimeException("No type defined for entity in map file");
        }

        // Deserialize general parameters
        entity.mapSpawned = true;
        entity.faction = (short) (long) entityData.get("faction");
        entity.spawn = spawnParserHelper.deserialize((JSONObject) entityData.get("spawn"));

        // Deserialize custom entity data
        if (entity instanceof EntityMapDataSerializer)
        {
            EntityMapDataSerializer serializer = (EntityMapDataSerializer) entity;
            serializer.deserialize(controller, map, entityData);
        }

        // Add to world
        respawnManager.respawn(new EntityPendingRespawn(controller, map, entity, 0, false));
    }

    /**
     * To be invoked once a map has finished loading.
     *
     * @param map the map to which this belongs
     */
    public void reset(WorldMap map)
    {
        quadTree = new QuadTree(map);
        entities = new ConcurrentHashMap<>();
        idCounterProvider.reset();
    }

    @Override
    public boolean containsId(short id)
    {
        return entities.containsKey(id);
    }

    /**
     * Entities should not be added this way, only through the respawn manager.
     *
     * @param entity
     * @return
     */
    protected boolean add(Entity entity)
    {
        // Fetch the next available identifier
        Short entityId = idCounterProvider.nextId(entity.id);

        // Check we found an identifier
        if (entityId == null)
        {
            LOG.error("Unable to create identifier for entity - {}", entity);
            return false;
        }

        // Assign id to entity
        entity.id = entityId;

        // Add mapping
        // TODO: consider removal of sync (entity), may be too excessive...
        synchronized (entity)
        {
            synchronized (this)
            {
                // Update state to created - for update to all players!
                entity.setState(EntityState.CREATED);

                // Add entity to pending map
                entities.put(entityId, entity);

                // Add to quadtree
                quadTree.update(entity);

                LOG.debug("Added entity to world - entity: {}", entity);
            }
        }

        return true;
    }

    protected synchronized void remove(Controller controller, Entity entity)
    {
        synchronized (entity)
        {
            if (entity.id != null && entities.containsKey(entity.id))
            {
                // Remove from quad-tree
                quadTree.remove(entity);

                // Mark entity for deletion
                entity.setState(EntityState.PENDING_DELETED);

                LOG.debug("Entity set for removal - ent id: {}", entity.id);

                // Invoke entity event handler
                entity.eventPendingDeleted(controller);
            }
        }
    }

    public QuadTree getQuadTree()
    {
        return quadTree;
    }

}
