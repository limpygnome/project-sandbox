package com.limpygnome.projectsandbox.server.entity.imp.living.pedestrian;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;

import static com.limpygnome.projectsandbox.server.constant.entity.PedestrianConstants.*;

/**
 * Created by limpygnome on 04/09/15.
 */
@EntityType(typeId = 510, typeName = "living/pedestrian/attacking")
public class AttackingPedestrian extends AbstractPedestrian
{

    public AttackingPedestrian()
    {
        super(
                ENT_WIDTH,
                ENT_HEIGHT,
                ATTACKING_PED_HEALTH,
                ATTACKING_PED_INVENTORY,
                ATTACKING_PED_ENGAGE_DISTANCE,
                ATTACKING_PED_FOLLOW_SPEED,
                ATTACKING_PED_FOLLOW_DISTANCE,
                ATTACKING_PED_ATTACK_DISTANCE,
                ATTACKING_PED_ATTACK_ROTATION_NOISE
        );
    }

    @Override
    public String friendlyName()
    {
        return "Attacking Pedestrian";
    }

}
