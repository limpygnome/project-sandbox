package com.projectsandbox.components.game.pedestrian;

import com.projectsandbox.components.server.entity.ai.IdleMode;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.constant.entity.PedestrianConstants;

/**
 * Created by limpygnome on 04/09/15.
 */
@EntityType(typeId = 510, typeName = "living/pedestrian/attacking")
public class AttackingPedestrian extends AbstractPedestrian
{

    public AttackingPedestrian(WorldMap map)
    {
        super(
                map,
                PedestrianConstants.ENT_WIDTH,
                PedestrianConstants.ENT_HEIGHT,
                PedestrianConstants.ATTACKING_PED_HEALTH,
                PedestrianConstants.ATTACKING_PED_INVENTORY,
                PedestrianConstants.ATTACKING_PED_ENGAGE_DISTANCE,
                PedestrianConstants.ATTACKING_PED_FOLLOW_SPEED,
                PedestrianConstants.ATTACKING_PED_FOLLOW_DISTANCE,
                0.0f,//ATTACKING_PED_ATTACK_DISTANCE,
                PedestrianConstants.ATTACKING_PED_ATTACK_ROTATION_NOISE,
                IdleMode.WALK// IdleMode.RETURN_TO_SPAWN
        );
    }

    @Override
    public String friendlyName()
    {
        return "Attacking Pedestrian";
    }

}
