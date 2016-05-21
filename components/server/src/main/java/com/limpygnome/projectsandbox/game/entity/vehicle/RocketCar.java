package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerEjectionComponent;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.RocketLauncher;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

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

        RocketLauncher rocketLauncher = new RocketLauncher();
        inventory.add(rocketLauncher);

        setInventory(0, inventory);
    }

}
