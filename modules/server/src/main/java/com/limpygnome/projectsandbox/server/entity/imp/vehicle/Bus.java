package com.limpygnome.projectsandbox.server.entity.imp.vehicle;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;

/**
 * Created by limpygnome on 01/07/15.
 */
@EntityType(typeId = 22, typeName = "vehicle/bus")
public class Bus extends AbstractVehicle
{
    public Bus()
    {
        super(
                (short) 28,
                (short) 96,
                new Vector2[]
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
                        }
        );

        accelerationFactor = 0.1f;
        deaccelerationMultiplier = 0.98f;
        steeringAngle = 1.4f;
        maxSpeed = 7.0f;
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Bus";
    }
}
