package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 20, typeName = "vehicle/ice-cream-van")
public class IceCreamVan extends AbstractVehicle
{

    public IceCreamVan(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 32,
                (short) 64,
                players,
                null,
                new Vector2[]
                {
                    new Vector2(+16.0f, +8.0f),
                    new Vector2(-16.0f, +8.0f),
                    new Vector2(+16.0f, -8.0f),
                    new Vector2(-16.0f, -8.0f)
                }
        );

        components.register(new PlayerVehicleMovementComponent(
                0.15f,      // Acceleration factor
                0.95f,      // Deacceleration multiplier
                0.9f,       // Steering angle
                5.0f        // Max speed
        ));

        setMaxHealth(300.0f);
    }

    public IceCreamVan(WorldMap map)
    {
        this(map, new PlayerInfo[4]);
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Ice Cream Truck";
    }

}
