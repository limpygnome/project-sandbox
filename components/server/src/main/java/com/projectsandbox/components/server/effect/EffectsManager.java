package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.network.packet.imp.effect.EffectUpdatesOutboundPacket;

import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.service.EventLogicCycleService;
import com.projectsandbox.components.server.world.map.WorldMap;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages and executes logic for effects.
 */
public class EffectsManager implements EventLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(EffectsManager.class);

    private Controller controller;
    private WorldMap map;

    private List<AbstractEffect> pendingSend;

    public EffectsManager(Controller controller, WorldMap map)
    {
        this.controller = controller;
        this.map = map;

        this.pendingSend = new LinkedList<>();
    }

    @Override
    public void logic()
    {
        // Send updates / pending effects
        try
        {
            sendUpdates();
        }
        catch (IOException e)
        {
            LOG.error("failed to send effect updates", e);
        }
    }

    private void sendUpdates() throws IOException
    {
        if (pendingSend.size() > 0)
        {
            Set<PlayerInfo> players = controller.playerService.getPlayers(map);

            for (PlayerInfo playerInfo : players)
            {
                // Build packet for player
                EffectUpdatesOutboundPacket packet = new EffectUpdatesOutboundPacket();
                packet.writeEffects(playerInfo, pendingSend);

                // Send packet
                controller.packetService.send(playerInfo, packet);
            }

            // Clear pending effects
            pendingSend.clear();
        }
    }

    public void add(AbstractEffect effect)
    {
        pendingSend.add(effect);
    }

}
