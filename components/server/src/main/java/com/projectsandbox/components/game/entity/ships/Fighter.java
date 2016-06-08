package com.projectsandbox.components.game.entity.ships;

import com.projectsandbox.components.game.inventory.item.jump.JumpDrive;
import com.projectsandbox.components.game.inventory.item.weapon.Gatling;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.game.component.PlayerEjectionComponent;
import com.projectsandbox.components.game.component.SpaceMovementComponent;
import com.projectsandbox.components.game.component.VelocityComponent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.game.inventory.item.weapon.RocketLauncher;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

@EntityType(typeId = 200, typeName = "ships/fighter")
public class Fighter extends PlayerEntity
{

    public Fighter(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 45,
                (short) 50,
                players,
                new Inventory[1]
        );

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

        setMaxHealth(150.0f);
    }

    public Fighter(WorldMap map)
    {
        this(map, new PlayerInfo[1]);
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
            inventory.add(new RocketLauncher(null));
            inventory.add(new JumpDrive(100000.0f, 1000.0f, 50.0f));
            inventory.add(new Gatling());
            setInventory(0, inventory);
        }
    }

}
