package com.projectsandbox.components.game.pickup;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.WorldMap;

import static com.projectsandbox.components.server.constant.entity.pickup.PickupConstants.HEALTH_WIDTH;
import static com.projectsandbox.components.server.constant.entity.pickup.PickupConstants.HEALTH_HEIGHT;

/**
 * Created by limpygnome on 06/07/15.
 */
@EntityType(typeId = 1201, typeName = "pickup/health")
public class HealthPickup extends AbstractPickup
{
    private float healthAmount;

    public HealthPickup()
    {
        super(
                HEALTH_WIDTH,
                HEALTH_HEIGHT
        );
    }

    @Override
    public void applyMapKeyValues(MapEntKV mapEntKV)
    {
        long respawnDelay = mapEntKV.getLong("pickup.respawn_delay");
        setRespawnDelay(respawnDelay);

        this.healthAmount = mapEntKV.getFloat("health_pickup.health");
    }

    @Override
    public boolean applyPickup(Controller controller, Entity entity)
    {
        // Check entity can receive pickup
        if  (
                entity instanceof PlayerEntity &&
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
