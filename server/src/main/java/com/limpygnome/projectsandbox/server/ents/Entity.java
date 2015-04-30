package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.server.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.Vertices;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.utils.CustomMath;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 0)
public strictfp abstract class Entity
{
    public static final short DEFAULT_FACTION = 0;
    
    // The unique ID for the entity
    public short id;
    
    public short faction;
    
    // The type of entity
    public short entityType;
    
    // State flags
    private StateChange state;
    /** Refer to {@link UpdateMasks} */
    public char updateMask;
    
    // State data
    public short width;
    public short height;
    public Vector2 position;
    public Vector2 positionNew;
    public Vertices cachedVertices;
    public float rotation;
    // -- -1 for godmode
    public float health;
    public float maxHealth;
    
    public Entity(short width, short height)
    {
        this.id = 0;
        this.faction = DEFAULT_FACTION;
        
        // Fetch entity ID
        final Class ENTITY_CLASS = getClass();
        
        // -- Check annotation present
        if (!ENTITY_CLASS.isAnnotationPresent(EntityType.class))
        {
            throw new IllegalArgumentException("No entity-type annotation present.");
        }
        
        // -- Read ID from annotation
        Annotation annotationEntityType = ENTITY_CLASS.getAnnotation(EntityType.class);
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
    }
    
    public void logic(Controller controller)
    {
        // Nothing by default...
    }
    
    public void rotation(float radians)
    {
        // Clamp within valid range
        radians = CustomMath.clampRepeat(
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
     * @param damage The damage; can be negative for health/healing.
     */
    public void damage(Controller controller, float damage)
    {
        // Check entity does not have godmode
        if (health == -1.0f)
        {
            return;
        }
        // Check damage is not 0.0 or infinity
        else if
        (
            health == 0.0f || health == Float.NaN ||
            health == Float.POSITIVE_INFINITY || 
            health == Float.NEGATIVE_INFINITY
        )
        {
            return;
        }
        
        // Apply damage
        health -= damage;
        
        // Update mask
        updateMask(UpdateMasks.HEALTH);

        // Check health
        if (health <= 0.0f)
        {
            // Entity is now dead!
            kill(controller);
        }
    }
    
    /**
     * Should be invoked to kill the ent; allows the ent to decide what to do.
     * 
     * @param controller
     */
    public void kill(Controller controller)
    {
        eventDeath(controller);
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
    
    /**
     * Event for death of entity.
     * 
     * Default behaviour is to delete entity. Method should be overridden to
     * change behaviour.
     * 
     * @param controller 
     */
    public void eventDeath(Controller controller)
    {
        // Default action is to respawn the entity
        controller.mapManager.main.spawn(this);
    }
    
    public void eventPacketEntCreated(List<Object> packetData)
    {
    }
    
    public void eventPacketEntUpdated(List<Object> packetData)
    {
    }
    
    public void eventPacketEntDeleted(List<Object> packetData)
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
    
    public abstract void reset();

    @Override
    public String toString()
    {
        return "[" + getClass().getName() + " - id: " + id + "]";
    }
}
