package com.projectsandbox.components.game.pickup;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityMapDataSerializer;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.json.simple.JSONObject;

import static com.projectsandbox.components.server.constant.entity.pickup.PickupConstants.HEALTH_WIDTH;
import static com.projectsandbox.components.server.constant.entity.pickup.PickupConstants.HEALTH_HEIGHT;

/**
 * Created by limpygnome on 06/07/15.
 */
@EntityType(typeId = 1201, typeName = "pickup/health")
public class HealthPickup extends AbstractPickup implements EntityMapDataSerializer
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
    public void serialize(Controller controller, WorldMap map, JSONObject entityData)
    {
        JSONObject healthData = new JSONObject();
        healthData.put("respawn-delay", getRespawnDelay());
        healthData.put("amount", healthAmount);

        // Attach health data to entity
        entityData.put("health", healthData);
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject entityData)
    {
        JSONObject healthData = (JSONObject) entityData.get("health");

        if (healthData != null)
        {
            long respawnDelay = (long) healthData.get("respawn-delay");
            setRespawnDelay(respawnDelay);
            this.healthAmount = (float) (double) healthData.get("amount");
        }
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
