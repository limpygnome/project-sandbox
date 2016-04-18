package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerEjectionComponent;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.map.CollisionMapResult;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

/**
 * Generic vehicle for players.
 */
public abstract class AbstractVehicle extends PlayerEntity
{

    public AbstractVehicle(WorldMap map, short width, short height, PlayerInfo[] players, Inventory[] inventories, Vector2[] playerEjectPositions)
    {
        super(map, width, height, players, inventories);

        if (players == null || players.length == 0)
        {
            throw new IllegalArgumentException("Players must be defined, even if null sized array; defines number of players able to use vehicle");
        }

        components.register(new PlayerEjectionComponent(this, playerEjectPositions));

    }

    @Override
    public synchronized String friendlyName()
    {
        PlayerInfo driver = getPlayer();

        if (driver != null)
        {
            return driver.session.getNickname();
        }
        else
        {
            return friendlyNameVehicle();
        }
    }

    public abstract String friendlyNameVehicle();

}
