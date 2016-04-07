package com.limpygnome.projectsandbox.server.network.packet.imp.effect;

import com.limpygnome.projectsandbox.server.effect.types.AbstractEffect;
import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;

import java.io.IOException;
import java.util.List;

/**
 * Created by limpygnome on 06/05/15.
 */
public class EffectUpdatesOutboundPacket extends OutboundPacket
{
    public EffectUpdatesOutboundPacket()
    {
        super((byte) 'Z');
    }

    public void writeEffects(List<AbstractEffect> effects) throws IOException
    {
        for (AbstractEffect effect : effects)
        {
            effect.writePacketData(packetData);
        }
    }
}
