package com.projectsandbox.components.game.vehicle;

import com.projectsandbox.components.game.PlayerEjectionComponent;
import com.projectsandbox.components.game.PlayerVehicleMovementComponent;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 20, typeName = "vehicle/ice-cream-van")
public class IceCreamVan extends PlayerEntity
{

    public IceCreamVan()
    {
        super(
                (short) 32,
                (short) 64
        );

        setMaxPlayers(4);

        components.add(new PlayerEjectionComponent(this, new Vector2[]
                {
                        new Vector2(+16.0f, +8.0f),
                        new Vector2(-16.0f, +8.0f),
                        new Vector2(+16.0f, -8.0f),
                        new Vector2(-16.0f, -8.0f)
                }));

        components.add(new PlayerVehicleMovementComponent(
                0.15f,      // Acceleration factor
                0.95f,      // Deacceleration multiplier
                0.9f,       // Steering angle
                5.0f        // Max speed
        ));

        setMaxHealth(300.0f);
    }

    @Override
    public String entityName()
    {
        return "Ice Cream Van";
    }

}
