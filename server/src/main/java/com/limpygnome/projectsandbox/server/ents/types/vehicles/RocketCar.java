package com.limpygnome.projectsandbox.server.ents.types.vehicles;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;

/**
 * Created by limpygnome on 01/07/15.
 */
@EntityType(typeId = 21)
public class RocketCar extends AbstractVehicle
{
    public RocketCar()
    {
        super(
                (short) 24,
                (short) 36,
                new Vector2[]
                        {
                                new Vector2(+16.0f, 0.0f),
                                new Vector2(-16.0f, 0.0f),
                        }
        );

        accelerationFactor = 0.4f;
        deaccelerationMultiplier = 0.9f;
        steeringAngle = 0.6f;
        maxSpeed = 12.0f;
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Rocket Car";
    }
}
