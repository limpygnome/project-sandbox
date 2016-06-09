package com.projectsandbox.components.game.inventory.item.weapon;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.respawn.pending.CurrentPositionRespawn;
import com.projectsandbox.components.game.entity.weapon.Rocket;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.player.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_LAUNCH_SPACING;

/**
 * Created by limpygnome on 26/06/15.
 */
@InventoryItemTypeId(typeId = 601)
public class RocketLauncherItem extends AbstractWeaponItem
{
    public static final long serialVersionUID = 1L;

    private final static Logger LOG = LogManager.getLogger(RocketLauncherItem.class);

    private final Vector2[] offsets;

    /**
     * Creates a new rocket launcher inventory item.
     *
     * @param offsets locations of where rockets are fired from; if null, only single rocket is fired from front-centre of ship
     */
    public RocketLauncherItem(Vector2[] offsets)
    {
        super(
                (short) 1,      // bullets per mag
                (short) 100,    // mags
                1,              // fire delay
                500             // reload delay
        );

        this.invokeType = InventoryInvokeType.FIRE_ONCE;
        this.offsets = offsets;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        Entity entityOwner = this.slot.inventory.parent;
        PlayerInfo playerInfoOwner = entityOwner.getPlayer();

        if (entityOwner != null)
        {
            // Project in front of player
            if (offsets != null)
            {
                for (Vector2 offset : offsets)
                {
                    fireRocket(controller, playerInfoOwner, entityOwner, offset);
                }
            }
            else
            {
                fireRocket(controller, playerInfoOwner, entityOwner, null);
            }
        }
        else
        {
            LOG.warn("unable to create rocket, owner not correctly set");
        }
    }

    private void fireRocket(Controller controller, PlayerInfo playerInfoOwner, Entity entityOwner, Vector2 offset)
    {
        // Create entity and put in front of owner/parent entity
        Entity rpg = new Rocket(entityOwner.map, controller, playerInfoOwner);
        rpg.projectInFrontOfEntity(entityOwner, ROCKET_LAUNCH_SPACING, offset);

        // Add rocket to world
        entityOwner.map.respawnManager.respawn(new CurrentPositionRespawn(controller, rpg));
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return String.format("%04d", mags + bullets);
    }

}
