package com.projectsandbox.components.server.inventory.item;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.game.effect.BulletEffect;
import com.projectsandbox.components.game.effect.TracerEffect;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.death.GunshotKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.casting.Casting;
import com.projectsandbox.components.server.entity.physics.casting.CastingResult;
import com.projectsandbox.components.server.entity.physics.casting.victims.EntityCastVictim;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.InventorySlotState;
import com.projectsandbox.components.server.inventory.WeaponConstants;
import com.projectsandbox.components.server.inventory.InventoryMergeResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An abstract weapon with bullets and magazines.
 */
public abstract class AbstractWeaponItem extends AbstractInventoryItem
{
    private final static Logger LOG = LogManager.getLogger(AbstractWeaponItem.class);

    public short bulletsPerMag;
    public short bullets;
    public short mags;
    
    public long fireDelay;
    public long reloadDelay;
    public transient long cooldownEnd;
    
    public float maxDistance;
    
    public AbstractWeaponItem(short bulletsPerMag, short mags, long fireDelay, long reloadDelay)
    {
        this.bullets = bulletsPerMag;
        this.bulletsPerMag = bulletsPerMag;
        this.mags = --mags;
        
        this.fireDelay = fireDelay;
        this.reloadDelay = reloadDelay;
        this.cooldownEnd = 0;
        
        this.maxDistance = WeaponConstants.DEFAULT_MAX_DISTANCE;
    }

    @Override
    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        LOG.debug("Invoke change - state: {}", invokeState);

        if (invokeState == InventoryInvokeState.INVOKE_ONCE)
        {
            fire(controller);
        }
    }

    @Override
    public void logic(Controller controller)
    {
        // Run logic for item
        super.logic(controller);

        // Check if state is on
        if (slot.invokeState == InventoryInvokeState.ON)
        {
            fire(controller);
        }
    }

    public synchronized void fire(Controller controller)
    {
        Entity owner = slot.inventory.parent;

        // Check we have an owner
        if (owner == null)
        {
            LOG.debug("Owner not set on weapon, cannot fire");
            return;
        }
        
        long currTime = System.currentTimeMillis();
        
        // Check for cooldown expire
        if (cooldownEnd != 0 && cooldownEnd < currTime)
        {
            cooldownEnd = 0;
        }
        
        // Check if cooldown in effect
        if (cooldownEnd == 0 && bullets > 0)
        {
            // Use up a bullet
            bullets--;
            
            // Check if we need to use up a mag
            if (bullets == 0 && mags > 0)
            {
                // AI receives unlimited mags...
                if (!owner.isAi())
                {
                    mags--;
                }

                bullets += bulletsPerMag;
                
                // Add reload delay
                if (reloadDelay > 0)
                {
                    cooldownEnd = currTime + reloadDelay;
                }
            }
            else if (fireDelay > 0)
            {
                // Add fire delay
                cooldownEnd = currTime + fireDelay;
            }

            // Fire bullet - implementation can be overridden
            fireBullet(controller);

            // Set slot to updated
            slot.setState(InventorySlotState.UPDATED);
        }
    }

    protected void fireBullet(Controller controller)
    {
        // Cast bullet to find collision point
        CastingResult castingResult = Casting.cast(controller, slot.inventory.parent, slot.inventory.parent.rotation, maxDistance);

        LOG.debug("Bullet casting result: {}", castingResult);

        if (castingResult.collision)
        {
            if (castingResult.victim instanceof EntityCastVictim)
            {
                // Inflict damage on the entity
                EntityCastVictim victim = (EntityCastVictim) castingResult.victim;
                fireDamageEntity(controller, castingResult, victim);
            }
        }

        // Render effect
        createBulletShotEffects(controller, slot.inventory.parent.positionNew, castingResult.x, castingResult.y);
    }

    protected void createBulletShotEffects(Controller controller, Vector2 source, float destX, float destY)
    {
        Entity entity = slot.inventory.parent;

        if (entity != null)
        {
            entity.map.effectsManager.add(new BulletEffect(destX, destY));
            entity.map.effectsManager.add(new TracerEffect(source, new Vector2(destX, destY)));
        }
    }

    protected void fireDamageEntity(Controller controller, CastingResult castingResult, EntityCastVictim victim)
    {
        victim.entity.damage(controller, slot.inventory.parent, 10.0f, GunshotKiller.class);
    }

    @Override
    public InventoryMergeResult merge(AbstractInventoryItem item)
    {
        // This check should not be needed, but just for sanity purposes
        if (item instanceof AbstractWeaponItem)
        {
            AbstractWeaponItem weapon = (AbstractWeaponItem) item;
            
            // Add bullets from item
            short combinedBullets = (short) (this.bullets + weapon.bullets);
            
            short newMags = (short) (combinedBullets / this.bulletsPerMag);
            short newBullets = (short) (combinedBullets % this.bulletsPerMag);
            
            // Add magazines from item
            this.mags += weapon.mags + newMags;
            this.bullets = newBullets;
            
            return InventoryMergeResult.MERGED;
        }
        else
        {
            throw new IllegalArgumentException("Attempted to merge different inventory item type as weapon");
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return String.format("%02d %04d", mags, bullets);
    }
}
