package com.limpygnome.projectsandbox.server.entity.imp.ships;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.imp.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

@EntityType(typeId = 200, typeName = "ships/yutamo-c1")
public class YutamoC1 extends AbstractVehicle
{

    public YutamoC1(WorldMap map, PlayerInfo playerInfo)
    {
        super(
                map,
                playerInfo,
                (short) 90,
                (short) 100,
                new Vector2[]
                {
                        new Vector2(+138.0f, +110.0f)
                }
        );

        accelerationFactor = 0.5f;
        deaccelerationMultiplier = 0.95f;
        steeringAngle = 0.9f;
        maxSpeed = 25.0f;
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Yutamo C1";
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        // Set player to use this entity
        controller.playerService.setPlayerEnt(players[0], this);

        super.eventSpawn(controller, spawn);
    }

}
