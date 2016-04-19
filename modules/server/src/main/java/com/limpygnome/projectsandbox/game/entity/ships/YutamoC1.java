package com.limpygnome.projectsandbox.game.entity.ships;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerEjectionComponent;
import com.limpygnome.projectsandbox.server.entity.component.imp.SpaceMovementComponent;
import com.limpygnome.projectsandbox.server.entity.component.imp.VelocityComponent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.RocketLauncher;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

@EntityType(typeId = 200, typeName = "ships/yutamo-c1")
public class YutamoC1 extends PlayerEntity
{

    public YutamoC1(WorldMap map, PlayerInfo[] players)
    {
        super(
                map,
                (short) 45,
                (short) 50,
                players,
                new Inventory[1]
        );

        components.register(new PlayerEjectionComponent(this, new Vector2[]
        {
                new Vector2(0.0f, 50.0f)
        }));

        components.register(new VelocityComponent(
                500.0f      // Mass
        ));

        components.register(new SpaceMovementComponent(35.0f));

        setMaxHealth(150.0f);
    }

    public YutamoC1(WorldMap map)
    {
        this(map, new PlayerInfo[1]);
    }

    @Override
    public String entityName()
    {
        return "Yutamo C1";
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        super.eventSpawn(controller, spawn);

        // Add inventory if not already set
        Inventory inventory = getInventory();

        if (inventory == null)
        {
            inventory = new Inventory(this);
            inventory.add(new RocketLauncher());
            setInventory(0, inventory);
        }
    }

}
