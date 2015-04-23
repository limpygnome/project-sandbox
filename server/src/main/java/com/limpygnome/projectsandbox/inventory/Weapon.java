package com.limpygnome.projectsandbox.inventory;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.physics.Casting;
import com.limpygnome.projectsandbox.ents.physics.CastingResult;

/**
 *
 * @author limpygnome
 */
public abstract class Weapon extends InventoryItem
{   
    public short bullets;
    public short bulletsPerMag;
    public short mags;
    
    public long fireDelay;
    public long reloadDelay;
    public long cooldownEnd;
    
    public float step;
    public float maxDistance;
    
    public Weapon(Inventory inventory, short bulletsPerMag, short mags, long fireDelay, long reloadDelay)
    {
        super(inventory);
        
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
        Entity owner = inventory.owner;
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
    public InventoryMergeResult merge(InventoryItem item)
    {
        // This check should not be needed, but just for sanity purposes
        if (item instanceof Weapon)
        {
            Weapon weapon = (Weapon) item;
            
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
