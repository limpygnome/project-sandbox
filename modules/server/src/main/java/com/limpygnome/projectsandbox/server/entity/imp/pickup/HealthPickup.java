package com.limpygnome.projectsandbox.server.entity.imp.pickup;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.imp.living.Player;
import com.limpygnome.projectsandbox.server.entity.imp.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.world.map.MapEntKV;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

import static com.limpygnome.projectsandbox.server.constant.entity.pickup.PickupConstants.*;

/**
 * Created by limpygnome on 06/07/15.
 */
@EntityType(typeId = 1201, typeName = "pickup/health")
public class HealthPickup extends AbstractPickup
{
    private float healthAmount;

    public HealthPickup(WorldMap map, MapEntKV mapEntKV)
    {
        super(
                map,
                HEALTH_WIDTH,
                HEALTH_HEIGHT,
                mapEntKV.getLong("pickup.respawn_delay")
        );

        this.healthAmount = mapEntKV.getFloat("health_pickup.health");
    }

    @Override
    public boolean applyPickup(Controller controller, Entity entity)
    {
        // Check entity can receive pickup
        if  (
                (entity instanceof Player || entity instanceof AbstractVehicle) &&
                entity.health > 0.0f && entity.health < entity.maxHealth
            )
        {
            // Calculate how much health would be gained from the pickup; if no health, don't bother; this will also
            // limit the applied health within and up to the max health.
            float maxHealthGain = entity.maxHealth - entity.health;
            float healthGained;

            if (healthAmount > maxHealthGain)
            {
                healthGained = maxHealthGain;
            }
            else
            {
                healthGained = healthAmount;
            }

            entity.damage(controller, this, -healthGained, null);
            return true;
        }

        return false;
    }

    @Override
    public String friendlyName()
    {
        return "Health Pickup";
    }
}
