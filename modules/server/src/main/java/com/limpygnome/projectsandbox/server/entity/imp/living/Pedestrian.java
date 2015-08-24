package com.limpygnome.projectsandbox.server.entity.imp.living;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

/**
 * Looks and acts as a player, but using AI.
 */
@EntityType(typeId = 510, typeName = "living/pedestrian")
public class Pedestrian extends Entity
{
    public Pedestrian()
    {
        super((short) 16, (short) 9);

        setMaxHealth(80.0f);
    }

    @Override
    public String friendlyName()
    {
        return "Pedestrian";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }
}
