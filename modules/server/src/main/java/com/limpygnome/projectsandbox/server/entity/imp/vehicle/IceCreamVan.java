package com.limpygnome.projectsandbox.server.entity.imp.vehicle;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
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

    public IceCreamVan(WorldMap map, PlayerInfo playerInfo)
    {
        super(
                map,
                playerInfo,
                (short) 32,
                (short) 64,
                new Vector2[]
                {
                    new Vector2(+16.0f, +8.0f),
                    new Vector2(-16.0f, +8.0f),
                    new Vector2(+16.0f, -8.0f),
                    new Vector2(-16.0f, -8.0f)
                }
        );
        
        accelerationFactor = 0.15f;
        deaccelerationMultiplier = 0.95f;
        steeringAngle = 0.9f;
        maxSpeed = 5.0f;
    }

    public IceCreamVan(WorldMap map)
    {
        this(map, null);
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Ice Cream Truck";
    }

}
