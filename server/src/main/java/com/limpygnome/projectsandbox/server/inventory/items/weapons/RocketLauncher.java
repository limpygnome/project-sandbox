package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.types.weapons.Rocket;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeType;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.limpygnome.projectsandbox.server.constants.weapons.RocketConstants.ROCKET_LAUNCH_SPACING;

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
                (short) 10, // mags
                1,          // fire delay
                500         // reload delay
        );

        this.invokeType = InventoryInvokeType.TOGGLE;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        Entity owner = this.slot.inventory.parent;
        PlayerInfo[] playerInfoOwners = owner.getPlayers();

        if (owner != null && playerInfoOwners != null && playerInfoOwners.length > 0)
        {
            // Create Rocket
            Entity rpg = new Rocket(controller, playerInfoOwners[0]);

            // Project in front of player
            rpg.projectInFrontOfEntity(owner, ROCKET_LAUNCH_SPACING);

            // Create rocket entity
            controller.entityManager.add(rpg);
        }
        else
        {
            LOG.debug("Unable to create rocket, owner not correctly set");
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return String.format("%04d", mags);
    }

}
