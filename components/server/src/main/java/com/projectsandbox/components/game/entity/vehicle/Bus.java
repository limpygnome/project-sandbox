package com.projectsandbox.components.game.entity.vehicle;

import com.projectsandbox.components.server.entity.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.component.imp.PlayerEjectionComponent;
import com.projectsandbox.components.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

@EntityType(typeId = 22, typeName = "vehicle/bus")
public class Bus extends PlayerEntity
{
    public Bus(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 28,
                (short) 96,
                players,
                null
        );

        components.add(new PlayerEjectionComponent(this, new Vector2[]
                {
                        new Vector2(+16.0f, +24.0f),
                        new Vector2(-16.0f, +24.0f),

                        new Vector2(+16.0f, +16.0f),
                        new Vector2(-16.0f, +16.0f),

                        new Vector2(+16.0f, +8.0f),
                        new Vector2(-16.0f, +8.0f),

                        new Vector2(+16.0f, -8.0f),
                        new Vector2(-16.0f, -8.0f),

                        new Vector2(+16.0f, -18.0f),
                        new Vector2(-16.0f, -18.0f),

                        new Vector2(+16.0f, -24.0f),
                        new Vector2(-16.0f, -24.0f)
                }));

        components.add(new PlayerVehicleMovementComponent(
                0.1f,       // Acceleration factor
                0.98f,      // Deacceleration multiplier
                1.4f,       // Steering angle
                7.0f        // Max speed
        ));

        setMaxHealth(400.0f);
    }

    public Bus(WorldMap map)
    {
        this(map, new PlayerInfo[12]);
    }

    @Override
    public String entityName()
    {
        return "Bus";
    }

}
