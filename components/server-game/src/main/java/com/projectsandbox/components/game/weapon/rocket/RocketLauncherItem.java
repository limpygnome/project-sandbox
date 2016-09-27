package com.projectsandbox.components.game.weapon.rocket;

import com.projectsandbox.components.server.entity.respawn.pending.ProjectInFrontOfEntityRespawn;
import com.projectsandbox.components.server.inventory.item.AbstractWeaponItem;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.respawn.pending.CurrentPositionRespawn;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
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
        // Create rocket
        Rocket rocket = new Rocket(controller, playerInfoOwner);

        // Add rocket to world
        WorldMap map = entityOwner.map;
        controller.respawnManager.respawn(new ProjectInFrontOfEntityRespawn(controller, map, rocket, entityOwner, ROCKET_LAUNCH_SPACING, offset));
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return String.format("%04d", mags + bullets);
    }

}
