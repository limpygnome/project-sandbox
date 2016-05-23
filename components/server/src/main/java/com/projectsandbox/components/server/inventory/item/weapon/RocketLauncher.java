package com.projectsandbox.components.server.inventory.item.weapon;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
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
public class RocketLauncher extends AbstractWeapon
{
    public static final long serialVersionUID = 1L;

    private final static Logger LOG = LogManager.getLogger(RocketLauncher.class);

    public RocketLauncher()
    {
        super(
                (short) 1,  // bullets per mag
                (short) 100, // mags
                1,          // fire delay
                500         // reload delay
        );

        this.invokeType = InventoryInvokeType.FIRE_ONCE;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        Entity owner = this.slot.inventory.parent;
        PlayerInfo[] playerInfoOwners = owner.getPlayers();
        PlayerInfo playerInfoOwner = (playerInfoOwners != null && playerInfoOwners.length > 0 ? playerInfoOwners[0] : null);

        if (owner != null)
        {
            // Create Rocket
            Entity rpg = new Rocket(owner.map, controller, playerInfoOwner);

            // Project in front of player
            rpg.projectInFrontOfEntity(owner, ROCKET_LAUNCH_SPACING);

            // Create rocket entity
            owner.map.respawnManager.respawn(new CurrentPositionRespawn(controller, rpg));
        }
        else
        {
            LOG.debug("unable to create rocket, owner not correctly set");
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return String.format("%04d", mags + bullets);
    }

}
