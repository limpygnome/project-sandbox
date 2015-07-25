package com.limpygnome.projectsandbox.server.inventory.item.weapon;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effect.types.ExplosionEffect;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.death.SuicideVestKiller;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.inventory.annotation.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.InventoryInvokeType;

import static com.limpygnome.projectsandbox.server.constant.weapon.SuicideVestConstants.*;

/**
 * Created by limpygnome on 30/06/15.
 */
@InventoryItemTypeId(typeId = 610)
public class SuicideVest extends AbstractWeapon
{
    public static final long serialVersionUID = 1L;

    private long gameTimeDetonation;

    public SuicideVest()
    {
        super(
                (short) 1,  // bullets per mag
                (short) 1,  // mags
                0,          // fire delay
                1000        // reload delay
        );

        this.invokeType = InventoryInvokeType.FIRE_ONCE;
        this.gameTimeDetonation = 0;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        this.gameTimeDetonation = controller.gameTime();
    }

    @Override
    public void logic(Controller controller)
    {
        // Check if to detonate yet...
        if (gameTimeDetonation != 0 && controller.gameTime() - gameTimeDetonation > SUICIDE_VEST_DELAY)
        {
            // Reset detonation just incase we cant remove it...
            this.gameTimeDetonation = 0;

            // Remove item from inventory
            slot.inventory.remove(this);

            // Detonate
            explode(controller);
        }

        super.logic(controller);
    }

    private void explode(Controller controller)
    {
        Entity parent = slot.inventory.parent;

        if (parent != null)
        {
            // Apply damage to entities
            DefaultProximity.applyLinearRadiusDamage(controller, parent, SUICIDE_VEST_RADIUS, SUICIDE_VEST_BLAST_DAMAGE, true, SuicideVestKiller.class);

            // Create explosion effect
            ExplosionEffect explosionEffect = new ExplosionEffect(
                    parent.positionNew.x,
                    parent.positionNew.y,
                    ExplosionEffect.SubType.SUICIDE_VEST
            );
            controller.effectsManager.add(explosionEffect);

            // Kill the current player
            parent.kill(controller, parent, SuicideVestKiller.class);
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return "";
    }

}
