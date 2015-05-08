package com.limpygnome.projectsandbox.server.packets.types.effects;

import com.limpygnome.projectsandbox.server.effects.types.AbstractEffect;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;

import java.io.IOException;
import java.util.LinkedList;
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
        LinkedList<Object> packetData = new LinkedList<>();

        for (AbstractEffect effect : effects)
        {
            effect.writePacketData(packetData);
        }

        write(packetData);
    }
}
