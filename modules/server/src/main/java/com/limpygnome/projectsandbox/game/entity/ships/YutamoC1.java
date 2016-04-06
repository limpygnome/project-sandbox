package com.limpygnome.projectsandbox.game.entity.ships;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.game.entity.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.RocketLauncher;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

@EntityType(typeId = 200, typeName = "ships/yutamo-c1")
public class YutamoC1 extends AbstractVehicle
{

    public YutamoC1(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 45,
                (short) 50,
                players,
                new Inventory[1],
                new Vector2[]
                {
                    new Vector2(0.0f, 50.0f)
                }
        );

        accelerationFactor = 0.5f;
        deaccelerationMultiplier = 0.95f;
        steeringAngle = 0.4f;
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
        super.eventSpawn(controller, spawn);

        Inventory inventory = new Inventory(this);
        inventory.add(new RocketLauncher());
        setInventory(0, inventory);
    }

}
