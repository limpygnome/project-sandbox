package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.respawn.SpawnParserHelper;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.util.IdCounterProvider;
import com.projectsandbox.components.server.util.counters.IdCounterConsumer;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limpygnome on 21/07/16.
 */
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
    public void serialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
        JSONArray rawEntities = (JSONArray) root.get("entities");
        JSONObject rawEntity;

        for (Object rawEntityObject : rawEntities)
        {
            rawEntity = (JSONObject) rawEntityObject;
            loadEntity(controller, map, rawEntity);
        }
    }

    protected void loadEntity(Controller controller, WorldMap map, JSONObject entData) throws IOException
    {
        // Parse faction
        short faction = (short) (long) entData.get("faction");

        // Parse spawn (optional)
        Spawn spawn = spawnParserHelper.parseSpawn((JSONObject) entData.get("spawn"));

        // Parse KV for spawning ent
        JSONObject rawKV = (JSONObject) entData.get("properties");
        MapEntKV mapEntKV;

        if (rawKV != null)
        {
            mapEntKV = loadEntityProperties(rawKV);
        }
        else
        {
            mapEntKV = null;
        }

        // Create instance
        Entity entity;

        if (entData.containsKey("typeName"))
        {
            String typeName = (String) entData.get("typeName");
            entity = entityTypeMappingStoreService.createByTypeName(typeName, mapEntKV);
        }
        else if (entData.containsKey("typeId"))
        {
            short typeId = (short) (long) entData.get("typeId");
            entity = entityTypeMappingStoreService.createByTypeId(typeId, mapEntKV);
        }
        else
        {
            throw new RuntimeException("No type defined for entity in map file");
        }

        // Set parameters
        entity.faction = faction;
        entity.spawn = spawn;

        // Add to world
        respawnManager.respawn(new EntityPendingRespawn(controller, map, entity, 0, false));
    }

    private MapEntKV loadEntityProperties(JSONObject rawProperties)
    {
        // TODO: refactor away from "KV" naming
        MapEntKV mapEntKV = new MapEntKV();

        // Parse each KV
        Iterator iterator = rawProperties.entrySet().iterator();
        java.util.Map.Entry<Object, Object> kv;
        String key;
        String value;

        while (iterator.hasNext())
        {
            // Read KV
            kv = (java.util.Map.Entry<Object, Object>) iterator.next();
            key = (String) kv.getKey();
            value = kv.getValue().toString();

            // Add to map
            mapEntKV.put(key, value);
        }

        return mapEntKV;
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
