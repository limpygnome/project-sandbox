package com.projectsandbox.components.game.vehicle.ship;

import com.projectsandbox.components.game.MapBoundsComponent;
import com.projectsandbox.components.game.flare.FlareItem;
import com.projectsandbox.components.game.forcefield.ForceFieldItem;
import com.projectsandbox.components.game.jumpdrive.JumpDriveItem;
import com.projectsandbox.components.game.weapon.GatlingItem;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.game.PlayerEjectionComponent;
import com.projectsandbox.components.game.SpaceMovementComponent;
import com.projectsandbox.components.game.VelocityComponent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.game.weapon.rocket.RocketLauncherItem;
import com.projectsandbox.components.server.world.spawn.Spawn;

@EntityType(typeId = 200, typeName = "ship/fighter")
public class Fighter extends PlayerEntity
{

    public Fighter()
    {
        super(
                (short) 45,
                (short) 50
        );

        setMaxPlayers(1);

        components.add(new PlayerEjectionComponent(this, new Vector2[]
                {
                        new Vector2(0.0f, 50.0f)
                }));

        components.add(new VelocityComponent(
                500.0f      // Mass
        ));

        components.add(new SpaceMovementComponent(
                40.0f,      // Speed limit
                1.0f,       // Acceleration
                0.2f        // Turning speed
        ));

        components.add(new MapBoundsComponent());

        setMaxHealth(150.0f);
    }

    @Override
    public String entityName()
    {
        return "Fighter";
    }

    @Override
    public synchronized void eventReset(Controller controller, Spawn spawn, boolean respawnAfterPersisted)
    {
        super.eventReset(controller, spawn, respawnAfterPersisted);

        if (!respawnAfterPersisted)
        {
            Inventory inventory = new Inventory(this);
            inventory.add(new RocketLauncherItem(null));
            inventory.add(new GatlingItem());

            inventory.add(new JumpDriveItem(
                    100000.0f,  // Max distance
                    1000.0f,    // Distance jumped for every cycle key held
                    50.0f       // Distance charged each cycle
            ));

            inventory.add(new ForceFieldItem(
                    1000.0f,    // Max health
                    1.0f,       // Regen health per cycle
                    5000L,      // Delay before recharging after depleted
                    4.0f        // Size multiplier
            ));

            inventory.add(new FlareItem(
                    (short) 12,     // Flares
                    10000,          // Lifespan
                    10.0f           // Max velocity
            ));

            setInventory(0, inventory);
        }
    }

}
