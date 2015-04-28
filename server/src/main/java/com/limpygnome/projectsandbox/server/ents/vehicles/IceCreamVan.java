package com.limpygnome.projectsandbox.server.ents.vehicles;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 20)
public class IceCreamVan extends AbstractCar
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
}
