package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.Vertices;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.packets.PacketData;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.utils.CustomMath;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
import com.limpygnome.projectsandbox.server.world.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 0)
public strictfp abstract class Entity
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    public static final short DEFAULT_FACTION = 0;
    
    // The unique ID for the entity
    public short id;

    // The ID of the faction to which the player belongs
    public short faction;
    
    // The type of entity
    public short entityType;

    // The default spawn
    public Spawn spawn;
    
    // State flags
    private StateChange state;
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

    // Physics
    public boolean physicsStatic;
    
    public Entity(short width, short height)
    {
        this.id = 0;
        this.faction = DEFAULT_FACTION;
        this.spawn = null;
        
        // Fetch entity ID
        final Class ENTITY_CLASS = getClass();
        
        // Read type from annotation
        Annotation annotationEntityType = getClass().getAnnotation(EntityType.class);
        EntityType entType = (EntityType) annotationEntityType;
        this.entityType = entType.typeId();
        
        // Set initial slotState
        this.state = StateChange.CREATED;
        
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
    
    public void logic(Controller controller)
    {
        // Nothing by default...
    }
    
    public void rotation(float radians)
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
    
    public void rebuildCachedVertices()
    {
        cachedVertices = new Vertices(this);
    }
    
    public void rotationOffset(float radians)
    {
        rotation(rotation + radians);
    }
    
    public void positionOffset(Vector2 offset)
    {
        position(positionNew.x + offset.x, positionNew.y + offset.y);
    }
    
    public void positionOffset(float x, float y)
    {
        position(positionNew.x + x, positionNew.y + y);
    }
    
    public void position(Vector2 position)
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
    public void position(float x, float y)
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
    
    public StateChange getState()
    {
        return state;
    }
    
    public synchronized void setState(StateChange state)
    {
        // Only allow delete slotState to progress
        // TODO: disallow created -> update change
        if (state == StateChange.UPDATED && this.state == StateChange.CREATED)
        {
            return;
        }
        
        if(this.state == StateChange.PENDING_DELETED && state == StateChange.DELETED)
        {
            this.state = state;
        }
        else if(this.state != StateChange.DELETED && this.state != StateChange.PENDING_DELETED)
        {
            this.state = state;
        }
    }
    
    public void setMaxHealth(float maxHealth)
    {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        updateMask(UpdateMasks.HEALTH);
    }
    
    /**
     * Inflicts damage on the entity.
     * 
     * @param controller
     * @param damage The damage; can be negative for health/healing
     * @param inflicter The entity causing damage, can be null
     * @param killerType The type of death in the event this Entity dies
     */
    public <T extends Class<? extends AbstractKiller>> void damage(Controller controller, Entity inflicter, float damage, T killerType)
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
    public <T extends Class<? extends AbstractKiller>> void kill(Controller controller, Entity inflicter, T killType)
    {
        AbstractKiller killer;

        // Create instance of killer type
        try
        {
            killer = killType.newInstance();

            // Setup victim/killer
            killer.victim = this;
            killer.killer = inflicter;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            LOG.error("Incorrectly setup killer class", e);
            throw new RuntimeException("Incorrectly setup killer class - " + killType.getName(), e);
        }

        // Inform killer(s) of their act
        killInformPlayerInfo(inflicter != null ? inflicter.getPlayers() : null, controller, killer, false);

        // Inform all associated players they've been killed
        killInformPlayerInfo(getPlayers(), controller, killer, true);

        // Raise death event for this entity
        eventDeath(controller, killer);
    }

    private void killInformPlayerInfo(PlayerInfo[] playerInfos, Controller controller, AbstractKiller killer, boolean isVictim)
    {
        if (playerInfos != null)
        {
            for (PlayerInfo playerInfo : playerInfos)
            {
                if (playerInfo != null)
                {
                    playerInfo.eventPlayerKilled(controller, killer, isVictim);
                }
            }
        }
    }
    
    public void updateMask(UpdateMasks... masks)
    {
        for (UpdateMasks mask : masks)
        {
            this.updateMask |= mask.MASK;
        }
        setState(StateChange.UPDATED);
    }
    
    public void resetUpdateMask()
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
    public void eventDeath(Controller controller, AbstractKiller killer)
    {
        // Default action is to respawn the entity
        controller.mapManager.main.spawn(this);
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
    
    public boolean eventActionKey(Entity cause)
    {
        // Does nothing by default...
        // Return true = handled, false = unhandled
        return false;
    }

    public void eventCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        if (entCollider != this)
        {
            // Push ent out by default
            positionOffset(result.mtv);
        }
    }

    public void eventSpawn()
    {
        // Nothing by default...
    }
    
    public void reset()
    {
        health = maxHealth;
    }

    public abstract String friendlyName();

    /**
     * Used to fetch all the associated players with this Entity.
     *
     * @return
     */
    public abstract PlayerInfo[] getPlayers();

    @Override
    public String toString()
    {
        return "[" + getClass().getName() + " - id: " + id + "]";
    }
}
