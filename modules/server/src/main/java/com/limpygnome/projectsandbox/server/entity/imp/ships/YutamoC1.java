package com.limpygnome.projectsandbox.server.entity.imp.ships;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.imp.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

@EntityType(typeId = 200, typeName = "ships/yutamo-c1")
public class YutamoC1 extends AbstractVehicle
{

    public YutamoC1(WorldMap map, PlayerInfo playerInfo)
    {
        super(
                map,
                playerInfo,
                (short) 179,
                (short) 202,
                new Vector2[]
                {
                        new Vector2(+138.0f, +110.0f)
                }
        );

        accelerationFactor = 0.15f;
        deaccelerationMultiplier = 0.95f;
        steeringAngle = 0.9f;
        maxSpeed = 5.0f;
    }

    @Override
    public String friendlyNameVehicle()
    {
        return "Yutamo C1";
    }

}
