package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.Casting;
import com.limpygnome.projectsandbox.server.ents.physics.CastingResult;
import com.limpygnome.projectsandbox.server.inventory.items.AbstractInventoryItem;
import com.limpygnome.projectsandbox.server.inventory.WeaponConstants;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryMergeResult;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractWeapon extends AbstractInventoryItem
{   
    public short bullets;
    public short bulletsPerMag;
    public short mags;
    
    public long fireDelay;
    public long reloadDelay;
    public long cooldownEnd;
    
    public float step;
    public float maxDistance;
    
    public AbstractWeapon(short bulletsPerMag, short mags, long fireDelay, long reloadDelay)
    {
        this.bullets = bulletsPerMag;
        this.bulletsPerMag = bulletsPerMag;
        this.mags = --mags;
        
        this.fireDelay = fireDelay;
        this.reloadDelay = reloadDelay;
        this.cooldownEnd = 0;
        
        this.step = WeaponConstants.DEFAULT_STEP;
        this.maxDistance = WeaponConstants.DEFAULT_MAX_DISTANCE;
    }
    
    public synchronized void fire(Controller controller)
    {
        Entity owner = slot.inventory.parent;
        if (owner == null)
        {
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
                mags--;
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
            
            // Cast bullet to find collision point
            CastingResult result = Casting.cast(
                    controller,
                    owner.positionNew.x,
                    owner.positionNew.y,
                    owner.rotation,
                    step,
                    maxDistance
            );
            
            // Create effect at collision point
            
            
        }
    }

    @Override
    public InventoryMergeResult merge(AbstractInventoryItem item)
    {
        // This check should not be needed, but just for sanity purposes
        if (item instanceof AbstractWeapon)
        {
            AbstractWeapon weapon = (AbstractWeapon) item;
            
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
}
