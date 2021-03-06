package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.component.ComponentCollection;
import com.projectsandbox.components.server.entity.component.event.CollisionEntityComponentEvent;
import com.projectsandbox.components.server.entity.component.event.CollisionMapComponentEvent;
import com.projectsandbox.components.server.entity.component.event.DamageComponentEvent;
import com.projectsandbox.components.server.entity.component.event.DeathComponentEvent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.component.event.ResetComponentEvent;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionTileMapResult;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.network.packet.PacketData;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.util.CustomMath;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Set;

import static com.projectsandbox.components.server.constant.PlayerConstants.DEFAULT_RESPAWN_TIME_MS;

/**
 * Base entity.
 */
@EntityType(typeId = 0, typeName = "")
public strictfp abstract class Entity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LogManager.getLogger(Entity.class);

    public static final short DEFAULT_FACTION = 0;

    /**
     * The map to which this entity belongs.
     */
    public transient WorldMap map;

    /**
     * Used to register components, which add behaviour to the entity.
     */
    public ComponentCollection components;
    
    // The unique ID for the entity
    public transient Short id;

    // The ID of the faction to which the player belongs
    public short factionId;

    // The type of entity
    public short entityType;

    // The default spawn
    public Spawn spawn;
    
    // State flags
    private transient EntityState state;
    /** Refer to {@link UpdateMasks} */
    public transient char updateMask;
    public boolean mapSpawned;

    // Size
    public short width;
    public short height;

    // Position
    // TODO: consider if position/positionNew need to be separate anymore...
    public Vector2 position;
    public Vector2 positionNew;
    public float rotation;
    public transient Vertices cachedVertices;

    // Health
    // -- -1 for godmode
    public float health;
    public float maxHealth;

    // Physics
    /**
     * When true, this entity cannot be moved.
     */
    public boolean physicsStatic;

    /**
     * When true, other entities can move through this entity.
     */
    public boolean physicsIntangible;

    public Entity(short width, short height)
    {
        this.map = null;
        this.id = null;
        this.factionId = DEFAULT_FACTION;
        this.spawn = null;
        this.components = new ComponentCollection();
        
        // Read type from annotation
        Annotation annotationEntityType = getClass().getAnnotation(EntityType.class);
        EntityType entType = (EntityType) annotationEntityType;
        this.entityType = entType.typeId();

        // Set initial state
        this.state = EntityState.CREATED;

        // Create initial/default slotState data
        this.width = width;
        this.height = height;
        
        this.position = new Vector2();
        this.positionNew = new Vector2();
        rebuildCachedVertices();
        this.rotation = 0.0f;
        
        this.health = 0.0f;
        this.maxHealth = 0.0f;

        this.physicsStatic = false;
    }
    
    public synchronized void rotation(float radians)
    {
        // Clamp within valid range
        radians = CustomMath.clamp(
                -CustomMath.PI_FLOAT,
                CustomMath.PI_FLOAT,
                radians
        );
        
        // Check the rotation will change
        if (rotation == radians)
        {
            return;
        }
        
        // Update rotation
        this.rotation = radians;
        
        // Rebuild cached vertices
        rebuildCachedVertices();
        
        // Update slotState
        updateMask(UpdateMasks.ROTATION);
    }
    
    public synchronized void rebuildCachedVertices()
    {
        cachedVertices = new Vertices(this);
    }
    
    public synchronized void rotationOffset(float radians)
    {
        rotation(rotation + radians);
    }
    
    public synchronized void positionOffset(Vector2 offset)
    {
        position(positionNew.x + offset.x, positionNew.y + offset.y);
    }
    
    public synchronized void positionOffset(float x, float y)
    {
        position(positionNew.x + x, positionNew.y + y);
    }
    
    public synchronized void position(Vector2 position)
    {
        position(position.x, position.y);
    }
    
    /**
     * Updates the position of the entity, as well as any needed slotState changes
     * needed.
     * 
     * @param x The new X position.
     * @param y The new Y position.
     */
    public synchronized void position(float x, float y)
    {
        boolean changeX = positionNew.x != x;
        boolean changeY = positionNew.y != y;
        
        if (!changeX && !changeY)
        {
            // No changes...
            return;
        }
        
        // Update position new
        positionNew.x = x;
        positionNew.y = y;
        
        // Rebuild cached vertices
        rebuildCachedVertices();

        // Update quadtree
        EntityMapData mapData = map.getEntityMapData();
        mapData.getQuadTree().update(this);
        
        // Update slotState
        if (changeX && changeY)
        {
            updateMask(UpdateMasks.X, UpdateMasks.Y);
        }
        else if (changeX)
        {
            updateMask(UpdateMasks.X);
        }
        else
        {
            updateMask(UpdateMasks.Y);
        }
    }

    public synchronized boolean isDeleted()
    {
        return state == EntityState.PENDING_DELETED || state == EntityState.DELETED || id == null;
    }
    
    public synchronized EntityState getState()
    {
        return state;
    }

    public synchronized void transitionState()
    {
        // Reset mask of properties updated
        resetUpdateMask();

        // Transition to next state...
        switch(state)
        {
            case CREATED:
                setState(EntityState.NONE);
                break;
            case PENDING_DELETED:
                setState(EntityState.DELETED);
                break;
            case UPDATED:
                setState(EntityState.NONE);
                break;
        }
    }
    
    public synchronized void setState(EntityState state)
    {
        // Determine if state change is allowed
        boolean transitionAllowed;

        if (this.state != null)
        {
            switch (this.state)
            {
                case CREATED:
                    transitionAllowed = (
                            state == EntityState.UPDATED || state == EntityState.PENDING_DELETED ||
                                    state == EntityState.DELETED || state == EntityState.NONE ||
                                    state == EntityState.CREATED
                    );
                    break;
                case UPDATED:
                    transitionAllowed = (state == EntityState.UPDATED || state == EntityState.PENDING_DELETED || state == EntityState.NONE);
                    break;
                case PENDING_DELETED:
                    transitionAllowed = (state == EntityState.PENDING_DELETED || state == EntityState.DELETED);

                    // Remove from quadtree...
                    if (transitionAllowed)
                    {
                        map.getEntityMapData().getQuadTree().remove(this);
                    }

                    break;
                case DELETED:
                    transitionAllowed = (state == EntityState.CREATED);
                    break;
                case NONE:
                    transitionAllowed = (state == EntityState.UPDATED || state == EntityState.PENDING_DELETED);
                    break;
                default:
                    transitionAllowed = false;
                    break;
            }
        }
        else
        {
            transitionAllowed = true;
        }

        // Update transition if allowed
        if (transitionAllowed)
        {
            this.state = state;
        }
        else
        {
            LOG.debug("Disallowed state transition - old state: {} -> new state: {}, ent: {}", this.state, state, this);
        }
    }
    
    public synchronized void setMaxHealth(float maxHealth)
    {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        updateMask(UpdateMasks.HEALTH);
    }

    public synchronized void setGodmode()
    {
        setMaxHealth(-1.0f);
    }
    
    /**
     * Inflicts damage on the entity.
     * 
     * @param controller
     * @param damage The damage; can be negative for health/healing
     * @param inflicter The entity causing damage, can be null
     * @param killerType The type of death in the event this Entity dies
     */
    public synchronized <T extends Class<? extends AbstractKiller>> void damage(Controller controller, Entity inflicter, float damage, T killerType)
    {
        // Check entity does not have godmode
        if (maxHealth <= 0.0f)
        {
            return;
        }

        // Invoke components to handle damage
        Set<DamageComponentEvent> handlers = components.fetch(DamageComponentEvent.class);
        for (DamageComponentEvent handler : handlers)
        {
            damage = handler.eventDamage(controller, inflicter, damage);
        }

        // Apply damage and set mask as updated
        if (damage != 0.0f)
        {
            health -= damage;
            updateMask(UpdateMasks.HEALTH);

            // Check if entity has died...
            if (health <= 0.0f)
            {
                // Entity is now dead!
                kill(controller, inflicter, killerType);
            }
        }
    }
    
    /**
     * Should be invoked to kill the ent; allows the ent to decide what to do.
     * 
     * @param controller
     * @param inflicter The entity killing this entity, can be null
     * @param killType The type of kill, in the event the player dies
     */
    public synchronized <T extends Class<? extends AbstractKiller>> void kill(Controller controller, Entity inflicter, T killType)
    {
        AbstractKiller death;

        // Create instance of death type
        try
        {
            death = killType.newInstance();

            // Setup death
            death.victim = this;
            death.killer = inflicter;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            LOG.error("Incorrectly setup death class", e);
            throw new RuntimeException("Incorrectly setup death class - " + killType.getName(), e);
        }

        // Inform all players of the kill/death
        if (inflicter != null)
        {
            PlayerInfo[] playerInfoInflicters = inflicter.getPlayers();
            PlayerInfo[] playerInfoVictims = getPlayers();

            if (killHasPlayers(playerInfoVictims))
            {
                boolean suicide = killIsSuicide(playerInfoInflicters, playerInfoVictims);

                // Inform inflicters of their kills
                if (!suicide && playerInfoInflicters != null)
                {
                    for (PlayerInfo playerInfoInflicter : playerInfoInflicters)
                    {
                        if (playerInfoInflicter != null)
                        {
                            playerInfoInflicter.eventPlayerKill(controller, death, playerInfoVictims);
                        }
                    }
                }

                // Inform victims of their killers
                for (PlayerInfo playerInfoVictim : playerInfoVictims)
                {
                    // Check player is actually in this entity, else it's a transient relationship i.e. an entity owned
                    // by player, such as rocket
                    if (playerInfoVictim != null && playerInfoVictim.entity == this)
                    {
                        playerInfoVictim.eventPlayerKilled(controller, death, playerInfoInflicters);
                    }
                }
            }
        }

        // Raise death event for this entity
        eventDeath(controller, death);
    }

    private boolean killIsSuicide(PlayerInfo[] playerInfosA, PlayerInfo[] playerInfosB)
    {
        // Check we have two sets of players
        if (playerInfosA == null || playerInfosB == null)
        {
            return false;
        }

        // See if we can find a match
        for (PlayerInfo playerInfoA : playerInfosA)
        {
            for (PlayerInfo playerInfoB : playerInfosB)
            {
                if (playerInfoA == playerInfoB)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean killHasPlayers(PlayerInfo[] playerInfos)
    {
        // Check initial array size etc
        if (playerInfos == null || playerInfos.length == 0)
        {
            return false;
        }

        // Check items
        for (PlayerInfo playerInfo : playerInfos)
        {
            if (playerInfo != null)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the entity from the world.
     */
    public synchronized void remove()
    {
        setState(EntityState.PENDING_DELETED);
    }
    
    public synchronized void updateMask(UpdateMasks... masks)
    {
        for (UpdateMasks mask : masks)
        {
            // Add to pending mask data
            this.updateMask |= mask.MASK;
        }

        // Set state to updated
        if (this.state != EntityState.CREATED && !isDeleted())
        {
            setState(EntityState.UPDATED);
        }
    }
    
    public synchronized void resetUpdateMask()
    {
        this.updateMask = 0;
    }

    public synchronized void eventLogic(Controller controller)
    {
        Set<LogicComponentEvent> callbacks = components.fetch(LogicComponentEvent.class);

        for (LogicComponentEvent component : callbacks)
        {
            component.eventLogic(controller, this);
        }
    }

    public void eventPendingDeleted(Controller controller) { }

    /**
     * Event for death of entity.
     * 
     * Default behaviour is to respawn entity. Method should be overridden to
     * change behaviour.
     * 
     * @param controller
     * @param killer The cause of death.
     */
    public synchronized void eventDeath(Controller controller, AbstractKiller killer)
    {
        // Respawn the entity
        controller.respawnManager.respawn(new EntityPendingRespawn(controller, map, this, DEFAULT_RESPAWN_TIME_MS, false));

        // Invoke callbacks
        Set<DeathComponentEvent> callbacks = components.fetch(DeathComponentEvent.class);
        for (DeathComponentEvent callback : callbacks)
        {
            callback.eventDeath(controller, this, killer);
        }
    }
    
    public void eventPacketEntCreated(PacketData packetData)
    {
    }
    
    public void eventPacketEntUpdated(PacketData packetData)
    {
    }
    
    public void eventPacketEntDeleted(PacketData packetData)
    {
    }

    public boolean isCollidable(Entity entityOther)
    {
        return !(physicsStatic || entityOther.physicsStatic) && !(physicsIntangible || entityOther.physicsIntangible);
    }

    public synchronized void eventCollisionEntity(Controller controller, Entity entityCollider, Entity entityVictim, Entity entityOther, CollisionResult result)
    {
        // This entity cannot be static and both entities cannot be intangible
        if (entityCollider != this && isCollidable(entityOther))
        {
            // Push ent out by default
            positionOffset(result.mtv);
        }

        // Invoke component event
        Set<CollisionEntityComponentEvent> callbacks = components.fetch(CollisionEntityComponentEvent.class);

        for (CollisionEntityComponentEvent component : callbacks)
        {
            component.eventCollisionEntity(controller, this, entityOther, result);
        }
    }

    public synchronized void eventCollisionMap(Controller controller, CollisionMapResult collisionMapResult)
    {
        // Perform default behaviour for tile maps
        if (collisionMapResult instanceof CollisionTileMapResult)
        {
            CollisionTileMapResult collisionTileMapResult = (CollisionTileMapResult) collisionMapResult;

            // Check if solid for collision response
            if (collisionTileMapResult.tileType.properties.solid)
            {
                positionOffset(collisionTileMapResult.result.mtv);
            }

            // Check if to apply damage
            if (collisionTileMapResult.tileType.properties.damage != 0)
            {
                // TODO: apply damage from tile - prolly move this to a component
            }
        }

        // Invoke component handlers
        Set<CollisionMapComponentEvent> callbacks = components.fetch(CollisionMapComponentEvent.class);

        for (CollisionMapComponentEvent component : callbacks)
        {
            component.eventCollisionMap(controller, this, collisionMapResult);
        }
    }

    /**
     * Invoked before the entity is respawned.
     *
     * @param respawnAfterPersisted indicates if entity is being respawned after being persisted
     */
    public synchronized void eventReset(Controller controller, Spawn spawn, boolean respawnAfterPersisted)
    {
        if (!respawnAfterPersisted)
        {
            // Reset health
            health = maxHealth;

            // Set position etc for spawn
            positionNew.x = spawn.x;
            positionNew.y = spawn.y;
            position.copy(positionNew);
            rotation = spawn.rotation;
        }

        // Set all masks as dirty
        updateMask(UpdateMasks.ALL_MASKS);

        // Invoke component event
        Set<ResetComponentEvent> callbacks = components.fetch(ResetComponentEvent.class);

        for (ResetComponentEvent component : callbacks)
        {
            component.eventReset(controller, this, respawnAfterPersisted);
        }

        // Rebuild collision vertices
        rebuildCachedVertices();
    }

    /**
     * Invoked after the entity has been added to the world.
     *
     * This should not be used to reset the state of the entity, instead use reset method.
     */
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
    }

    public abstract String friendlyName();

    /**
     * Used to fetch all the associated players with this Entity.
     *
     * @return players, or null
     */
    public abstract PlayerInfo[] getPlayers();

    /**
     * Retrieves the first player in players associated with this entity. THis player is considered the driver, or
     * primary player.
     *
     * @return player or null
     */
    public synchronized PlayerInfo getPlayer()
    {
        PlayerInfo[] players = getPlayers();
        return players != null && players.length >= 1 ? players[0] : null;
    }

    /**
     * Used to indicate if the entity is AI.
     *
     * @return
     */
    public boolean isAi()
    {
        return false;
    }

    /**
     * Used to indicate the speed of the entity, since different entities may have different mechanics for handling
     * physics.
     *
     * @return The speed of the entity
     */
    public float getSpeed()
    {
        return 0.0f;
    }

    @Override
    public String toString()
    {
        return "[" + getClass().getName() + " - id: " + id + "]";
    }

}
