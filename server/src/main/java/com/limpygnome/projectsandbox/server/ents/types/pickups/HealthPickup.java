package com.limpygnome.projectsandbox.server.ents.types.pickups;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
import com.limpygnome.projectsandbox.server.ents.types.living.Player;
import com.limpygnome.projectsandbox.server.ents.types.vehicles.AbstractVehicle;
import com.limpygnome.projectsandbox.server.world.MapEntKV;

/**
 * Created by limpygnome on 06/07/15.
 */
@EntityType(typeId = 1201, typeName = "pickup/health")
public class HealthPickup extends AbstractPickup
{
    private float healthAmount;

    public HealthPickup(MapEntKV mapEntKV)
    {
        super(mapEntKV.getLong("pickup.respawn_delay"));

        this.healthAmount = mapEntKV.getFloat("health_pickup.health");
    }

    @Override
    public boolean applyPickup(Controller controller, Entity entity)
    {
        StateChange entityState = entity.getState();

        // Check entity is player or vehicle
        if (!(entity instanceof Player || entity instanceof AbstractVehicle))
        {
            return false;
        }

        // Check entity can receive pickup
        if (entity.health > 0 && entityState != StateChange.PENDING_DELETED && entityState != StateChange.DELETED)
        {
            // Calculate how much health would be gained from the pickup; if no health, don't bother; this will also
            // limit the applied health within and up to the max health.
            float healthGained = entity.maxHealth - (entity.health + healthAmount);

            if (healthGained > 0.0f)
            {
                entity.damage(controller, this, -healthGained, null);
                return true;
            }
        }

        return true;
    }

    @Override
    public String friendlyName()
    {
        return "Health Pickup";
    }
}
