package com.projectsandbox.components.game.entity.vehicle;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.component.imp.PlayerEjectionComponent;
import com.projectsandbox.components.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.inventory.item.weapon.RocketLauncher;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

@EntityType(typeId = 21, typeName = "vehicle/rocket-car")
public class RocketCar extends PlayerEntity
{

    public RocketCar(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 24,
                (short) 36,
                players,
                new Inventory[1]
        );

        components.register(new PlayerEjectionComponent(this, new Vector2[]
        {
                new Vector2(+16.0f, 0.0f),
                new Vector2(-16.0f, 0.0f),
        }));

        components.register(new PlayerVehicleMovementComponent(
                0.4f,       // Acceleration factor
                0.9f,       // Deacceleration multiplier
                0.6f,       // Steering angle
                12.0f       // Max speed
        ));

        setMaxHealth(50.0f);
    }

    public RocketCar(WorldMap map)
    {
        this(map, new PlayerInfo[2]);
    }

    @Override
    public String entityName()
    {
        return "Rocket Car";
    }

    @Override
    public synchronized void eventReset(Controller controller, Spawn spawn)
    {
        super.eventReset(controller, spawn);

        // Load default inventory
        Inventory inventory = new Inventory(this);

        RocketLauncher rocketLauncher = new RocketLauncher(null);
        inventory.add(rocketLauncher);

        setInventory(0, inventory);
    }

}
