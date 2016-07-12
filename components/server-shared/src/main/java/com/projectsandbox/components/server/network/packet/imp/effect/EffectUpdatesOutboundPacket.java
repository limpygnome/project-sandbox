package com.projectsandbox.components.server.network.packet.imp.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.network.packet.OutboundPacket;
import com.projectsandbox.components.server.network.packet.imp.entity.EntityUpdatesOutboundPacket;
import com.projectsandbox.components.server.player.PlayerInfo;

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

    public void writeEffects(PlayerInfo playerInfo, List<AbstractEffect> effects) throws IOException
    {
        // Fetch player's current entity
        Entity entity = playerInfo.entity;

        for (AbstractEffect effect : effects)
        {
            if (effect.isWithinRenderDistance(entity.positionNew, EntityUpdatesOutboundPacket.RADIUS_ENTITY_UPDATES))
            {
                effect.writePacketData(packetData);
            }
        }
    }

}
