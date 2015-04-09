package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.ents.enums.StateChange;
import com.limpygnome.projectsandbox.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import com.limpygnome.projectsandbox.ents.physics.Vertices;
import com.limpygnome.projectsandbox.utils.CustomMath;
import java.lang.annotation.Annotation;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 0)
public strictfp abstract class Entity
{
    // The unique ID for the entity
    public short id;
    
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
    
    public Entity(short width, short height)
    {
        this.id = 0;
        
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
        
        // Set initial state
        this.state = StateChange.CREATED;
        
        // Create initial/default state data
        this.width = width;
        this.height = height;
        this.position = new Vector2();
        this.positionNew = new Vector2();
        this.cachedVertices = new Vertices(this);
        this.rotation = 0.0f;
        this.health = 0.0f;
    }
    
    public void logic(Controller controller)
    {
        // Nothing by default...
    }
    
    public void rotation(float radians)
    {
        // Update rotation
        this.rotation = CustomMath.clampRepeat(
                -CustomMath.PI_FLOAT,
                CustomMath.PI_FLOAT,
                radians
        );
        
        // Rebuild cached vertices
        cachedVertices = new Vertices(this);
        
        // Update state
        updateMask(UpdateMasks.ROTATION);
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
     * Updates the position of the entity, as well as any needed state changes
     * needed.
     * 
     * @param x The new X position.
     * @param y The new Y position.
     */
    public void position(float x, float y)
    {
        boolean changeX = positionNew.x == x;
        boolean changeY = positionNew.y == y;
        
        if (!changeX && !changeY)
        {
            // No changes...
            return;
        }
        
        // Update position new
        positionNew.x = x;
        positionNew.y = y;
        
        // Rebuild cached vertices
        cachedVertices = new Vertices(this);
        
        // Update state
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
        // Only allow delete state to progress
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
        if (health < 0.0f)
        {
            // Entity is now dead!
            eventDeath(controller);
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
        // Default action is to remove the entity
        setState(StateChange.PENDING_DELETED);
    }
    
    public byte[] eventPacketEntCreated()
    {
        return null;
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

    @Override
    public String toString()
    {
        return "[" + getClass().getName() + " - id: " + id + "]";
    }
}
