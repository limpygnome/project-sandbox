package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.annotations.EntityType;

/**
 *
 * @author limpygnome
 */
@EntityType(typeId = 20)
public class IceCreamVan extends AbstractCar
{
    public IceCreamVan()
    {
        super((short) 32, (short) 64);
        
        accelerationFactor = 0.15f;
        deaccelerationMultiplier = 0.95f;
        steeringAngle = 0.5f;
    }

    @Override
    public strictfp boolean eventActionKey(Entity cause)
    {
        System.out.println("received action key!");
        return true;
    }
}
