package com.limpygnome.projectsandbox.server.effects;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effects.types.AbstractEffect;
import com.limpygnome.projectsandbox.server.packets.types.effects.EffectUpdatesOutboundPacket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 06/05/15.
 */
public class EffectsManager
{
    public Controller controller;
    private List<AbstractEffect> pendingSend;

    public EffectsManager(Controller controller)
    {
        this.controller = controller;
        this.pendingSend = new LinkedList<>();
    }

    public void logic() throws IOException
    {
        // Send updates / pending effects
        sendUpdates();
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
