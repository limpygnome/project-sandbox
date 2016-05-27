package com.projectsandbox.components.game.entity.world;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Created by limpygnome on 29/04/16.
 */
@EntityType(typeId = 4000, typeName = "world/blackhole")
public class Blackhole extends Entity
{

    public Blackhole(WorldMap map)
    {
        super(map, (short) 32, (short) 32);

        this.physicsIntangible = true;
        this.physicsStatic = true;
    }

    @Override
    public String friendlyName()
    {
        return "Blackhole";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }

}
