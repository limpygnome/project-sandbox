package com.projectsandbox.components.server.map.editor.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.network.packet.PacketData;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.spawn.Faction;
import com.projectsandbox.components.server.world.spawn.Spawn;

import java.awt.*;

/**
 * Used to represent a spawn when editing a map.
 *
 * The faction of the entity should be the same as the faction used for the spawn.
 */
@EntityType(typeId = 902, typeName = "util/spawn-marker")
public class SpawnMarker extends Entity
{
    private Faction faction;
    private Spawn spawn;

    public SpawnMarker()
    {
        super((short) 32, (short) 32);

        // This entity should be untouchable...
        this.physicsStatic = true;
        this.physicsIntangible = true;
        setGodmode();
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        super.eventSpawn(controller, spawn);

        // Copy faction/spawn
        this.spawn = spawn;
        this.faction = map.getRespawnMapData().getFactionSpawns().get(super.faction);
    }

    @Override
    public synchronized void eventDeath(Controller controller, AbstractKiller killer)
    {
        // Remove associated spawn
        faction.removeSpawn(spawn);
    }

    @Override
    public void eventPacketEntCreated(PacketData packetData)
    {
        // Add faction colour
        Color colour = faction.getColour();

        // -- Typecast for safety...
        packetData.add((int) colour.getRed());
        packetData.add((int) colour.getBlue());
        packetData.add((int) colour.getGreen());
    }

    @Override
    public String friendlyName()
    {
        return "Spawn Marker";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }

}
