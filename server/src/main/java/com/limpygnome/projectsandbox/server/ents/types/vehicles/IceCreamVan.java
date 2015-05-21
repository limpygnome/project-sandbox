package com.limpygnome.projectsandbox.server.ents.types.vehicles;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 20)
public class IceCreamVan extends AbstractVehicle
{
    public IceCreamVan()
    {
        super(
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

    @Override
    public String friendlyNameVehicle()
    {
        return "Ice Cream Truck";
    }
}
