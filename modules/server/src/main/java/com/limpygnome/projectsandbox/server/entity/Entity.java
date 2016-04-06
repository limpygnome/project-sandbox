package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.game.entity.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.packet.PacketData;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.util.CustomMath;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;

import static com.limpygnome.projectsandbox.server.constant.PlayerConstants.*;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 0, typeName = "")
public strictfp abstract class Entity
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    public static final short DEFAULT_FACTION = 0;

    /**
     * The map to which this entity belongs.
     */
    public WorldMap map;
    
    // The unique ID for the entity
    public short id;

    // The ID of the faction to which the player belongs
    public short faction;
    
    // The type of entity
    public short entityType;

    // The default spawn
    public Spawn spawn;
    
    // State flags
    private EntityState state;
    /** Refer to {@link UpdateMasks} */
    public char updateMask;
    
    // Size
    public short width;
    public short height;

    // Position
    // TODO: consider if position/positionNew need to be separate
    public Vector2 position;
    public Vector2 positionNew;
    public Vertices cachedVertices;
    public float rotation;

    // Health
    // -- -1 for godmode
    public float health;
    public float maxHealth;

    /**
     * Used as a flag to track if the entity has died and not respawned. Hooks into updateMasks mechanism. When the
     * entity is next spawned, its flag is unset.
     */
    private boolean flagDead;

    // Physics
    /**
     * When true, this entity cannot be moved.
     */
    public boolean physicsStatic;

    /**
     * When true, other entities can move through this entity.
     */
    public boolean physicsIntangible;
    
    public Entity(WorldMap map, short width, short height)
    {
        this.map = map;
        this.id = 0;
        this.faction = DEFAULT_FACTION;
        this.spawn = null;
        
        // Read type from annotation
        Annotation annotationEntityType = getClass().getAnnotation(EntityType.class);
        EntityType entType = (EntityType) annotationEntityType;
        this.entityType = entType.typeId();
        
        // Set initial slotState
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
        this.flagDead = true;

        this.physicsStatic = false;
    }
    
    public synchronized void logic(Controller controller)
    {
        // Nothing by default...
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

    /**
     * Places this entity in front of the specified parent.
     *
     * @param parent The parent entity
     * @param spacing The spacing between the two ents
     */
    public synchronized void projectInFrontOfEntity(Entity parent, float spacing)
    {
        // Clone the rotation of the parent
        rotation(parent.rotation);

        // Calculate new position, so we're in front of parent
        Vector2 newPosition = parent.positionNew.clone();
        newPosition.offset(Vector2.vectorFromAngle(this.rotation, parent.height / 2.0f));
        newPosition.offset(Vector2.vectorFromAngle(this.rotation, height));
        newPosition.offset(Vector2.vectorFromAngle(this.rotation, spacing));

        if (parent instanceof AbstractVehicle)
        {
            // Add velocity?
            AbstractVehicle vehicle = (AbstractVehicle) parent;
            float speed = vehicle.getSpeed() + vehicle.getAccelerationFactor();

            if (speed > 0.0f)
            {
                newPosition.offset(Vector2.vectorFromAngle(this.rotation, speed));
            }
        }

        position(newPosition);
    }

    public synchronized boolean isDeleted()
    {
        return state == EntityState.PENDING_DELETED || state == EntityState.DELETED;
    }
    
    public synchronized EntityState getState()
    {
        return state;
    }
    
    public synchronized void setState(EntityState state)
    {
        // Determine if state change is allowed
        boolean transitionAllowed;

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
                transitionAllowed = (state == EntityState.DELETED);
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

        // Update transition if allowed
        if (transitionAllowed)
        {
            this.state = state;
        }
        else
        {
            LOG.error("Disallowed state transition - old state: {} -> new state: {}, ent: {}", this.state, state, this);
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

    protected synchronized void setDead(boolean dead)
    {
        // Set dead flag
        this.flagDead = dead;

        // Set that we've updated the flag
        updateMask(UpdateMasks.ALIVE);
    }
    
    /**
     * Inflicts damage on the entity.
     * 
     * @param controller
     * @param damage The damage; can be negative for health/healing
     * @param inflicter The entity causing damage, can be null
     * @param killerType The type of death in the event this Entity dies
     */
    public synchronized  <T extends Class<? extends AbstractKiller>> void damage(Controller controller, Entity inflicter, float damage, T killerType)
    {
        // Check entity does not have godmode
        if (maxHealth <= 0.0f)
        {
            return;
        }

        // Check health is not infinity
        if
        (
            health == Float.NaN ||
            health == Float.POSITIVE_INFINITY || 
            health == Float.NEGATIVE_INFINITY
        )
        {
            health = 0.0f;
        }
        
        // Apply damage
        health -= damage;
        
        // Update mask
        updateMask(UpdateMasks.HEALTH);

        // Check health
        if (health <= 0.0f)
        {
            // Entity is now dead!
            kill(controller, inflicter, killerType);
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
        eventHandleDeath(controller, death);
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

    /**
     * Retrieves the inventory for a player associated with this instance.
     *
     * @param playerInfo
     * @return Instance, or null if no inventory is available / not allowed.
     */
    public Inventory retrieveInventory(PlayerInfo playerInfo)
    {
        // No inventory by default
        return null;
    }

    public void eventPendingDeleted(Controller controller) { }

    /**
     * Event for death of entity.
     * 
     * Default behaviour is to delete entity. Method should be overridden to
     * change behaviour.
     * 
     * @param controller
     * @param killer The cause of death.
     */
    public synchronized void eventHandleDeath(Controller controller, AbstractKiller killer)
    {
        // Set internal flag
        this.flagDead = true;

        // Default action is to respawn the entity
        map.respawnManager.respawn(new EntityPendingRespawn(controller, this, DEFAULT_RESPAWN_TIME_MS));
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

    public synchronized void eventHandleCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        // This entity cannot be static and both entities cannot be intangible
        if  (
                (
                    entCollider != this || entOther.physicsStatic
                )
                    &&
                (
                    !physicsStatic && !(physicsIntangible || entOther.physicsIntangible)
                )
            )
        {
            // Push ent out by default
            positionOffset(result.mtv);
        }
    }

    public synchronized void eventHandleCollisionMap(Controller controller, CollisionResultMap collisionResultMap)
    {
        // Check if solid for collision response
        if (collisionResultMap.tileType.properties.solid)
        {
            positionOffset(collisionResultMap.result.mtv);
        }

        // Check if to apply damage
        if (collisionResultMap.tileType.properties.damage != 0)
        {
            // TODO: apply damage from tile
        }
    }

    /**
     * Invoked before the entity is respawned.
     */
    public synchronized void eventReset(Controller controller, Spawn spawn)
    {
        health = maxHealth;

        // Set position etc for spawn
        positionNew.x = spawn.x;
        positionNew.y = spawn.y;
        position.copy(positionNew);
        rotation = spawn.rotation;
        updateMask(UpdateMasks.ALL_MASKS);
    }

    /**
     * Invoked after the entity has been spawned, and added to the world.
     */
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        flagDead = false;
    }

    public abstract String friendlyName();

    /**
     * Used to fetch all the associated players with this Entity.
     *
     * @return
     */
    public abstract PlayerInfo[] getPlayers();

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

    /**
     * Indicates if the current entity is dead.
     *
     * @return True = dead, false = not dead.
     */
    public boolean isDead()
    {
        return flagDead;
    }

    @Override
    public synchronized String toString()
    {
        return "[" + getClass().getName() + " - id: " + id + "]";
    }
}
