package com.limpygnome.projectsandbox.server.ents.types.weapons;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effects.types.ExplosionEffect;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.death.ExplosionKiller;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

import java.util.List;

import static com.limpygnome.projectsandbox.server.constants.WeaponConstants.*;

/**
 * An RPG fired by a rocket weapon.
 */
@EntityType(typeId = 600)
public class RPG extends Entity
{
    public RPG()
    {
        super((short) 9, (short) 12);
    }

    @Override
    public String friendlyName()
    {
        return "RPG";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[0];
    }

    @Override
    public void logic(Controller controller)
    {
        // Check if RPG has expired
        // TODO: complete this

        super.logic(controller);
    }

    @Override
    public void eventCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        // Fetch entities within blast radius
        List<ProximityResult> proximityResults = DefaultProximity.nearbyEnts(controller, this, RPG_BLAST_RADIUS, true, false);

        // Apply damage to entities
        float damage;
        for (ProximityResult proximityResult : proximityResults)
        {
            // Calculate damage based on distance
            damage = (proximityResult.distance / RPG_BLAST_RADIUS) * RPG_BLAST_DAMAGE;

            // Apply damage
            entOther.damage(controller, this, damage, ExplosionKiller.class);
        }

        // Create explosion effect
        controller.effectsManager.add(new ExplosionEffect(this.positionNew.x, this.positionNew.y, ExplosionEffect.SubType.RPG));
    }
}
