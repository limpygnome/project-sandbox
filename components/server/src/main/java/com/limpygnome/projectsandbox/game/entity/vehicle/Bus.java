package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerEjectionComponent;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

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

        components.register(new PlayerEjectionComponent(this, new Vector2[]
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

        components.register(new PlayerVehicleMovementComponent(
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
