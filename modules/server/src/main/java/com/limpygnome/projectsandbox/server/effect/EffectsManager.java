package com.limpygnome.projectsandbox.server.effect;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effect.types.AbstractEffect;
import com.limpygnome.projectsandbox.server.packet.imp.effect.EffectUpdatesOutboundPacket;

import com.limpygnome.projectsandbox.server.service.LogicService;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 06/05/15.
 */
@Service
public class EffectsManager implements LogicService
{
    private final static Logger LOG = LogManager.getLogger(EffectsManager.class);

    @Autowired
    public Controller controller;

    private List<AbstractEffect> pendingSend;

    public EffectsManager()
    {
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
        // TODO: only send to players within a certain distance (?)

        if (pendingSend.size() > 0)
        {
            // Build packet from buffer and reset
            EffectUpdatesOutboundPacket packet = new EffectUpdatesOutboundPacket();
            packet.writeEffects(pendingSend);
            pendingSend.clear();

            // Broadcast to all players
            controller.playerManager.broadcast(packet);
        }
    }

    public void add(AbstractEffect effect)
    {
        pendingSend.add(effect);
    }

}
