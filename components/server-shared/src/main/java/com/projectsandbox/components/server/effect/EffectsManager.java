package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.network.packet.PacketService;
import com.projectsandbox.components.server.network.packet.imp.effect.EffectUpdatesOutboundPacket;

import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerService;
import com.projectsandbox.components.server.service.EventLogicCycleService;
import java.io.IOException;
import java.util.Set;

import com.projectsandbox.components.server.service.EventMapLogicCycleService;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages and executes logic for effects.
 */
@Component
public class EffectsManager implements EventMapLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(EffectsManager.class);

    @Autowired
    private MapService mapService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PacketService packetService;

    @Override
    public void logic(WorldMap map)
    {
        EffectsMapData mapData = map.getEffectsMapData();

        // Send updates / pending effects
        try
        {
            sendUpdates(map, mapData);
        }
        catch (IOException e)
        {
            LOG.error("failed to send effect updates", e);
        }
    }

    private void sendUpdates(WorldMap map, EffectsMapData mapData) throws IOException
    {
        if (mapData.pendingSend.size() > 0)
        {
            Set<PlayerInfo> players = playerService.getPlayers(map);

            for (PlayerInfo playerInfo : players)
            {
                // Build packet for player
                EffectUpdatesOutboundPacket packet = new EffectUpdatesOutboundPacket();
                packet.writeEffects(playerInfo, mapData.pendingSend);

                // Send packet
                packetService.send(playerInfo, packet);
            }

            // Clear pending effects
            mapData.pendingSend.clear();
        }
    }

    public void add(WorldMap map, AbstractEffect effect)
    {
        EffectsMapData mapData = map.getEffectsMapData();
        mapData.pendingSend.add(effect);
    }

}
