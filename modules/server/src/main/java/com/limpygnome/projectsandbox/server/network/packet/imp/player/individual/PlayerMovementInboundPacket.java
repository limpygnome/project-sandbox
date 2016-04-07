package com.limpygnome.projectsandbox.server.network.packet.imp.player.individual;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.network.packet.InboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.PacketParseException;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

/**
 * A packet sent by the client to update their movement slotState.
 * 
 * @author limpygnome
 */
public class PlayerMovementInboundPacket extends InboundPacket
{
    private final static Logger LOG = LogManager.getLogger(PlayerMovementInboundPacket.class);

    public short id;
    public short keys;

    @Override
    public void parse(Controller controller, WebSocket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        // Check length
        if (data.length != 6)
        {
            throw new PacketParseException("Incorrect length", data);
        }

        // Parse data
        id = bb.getShort(2);
        keys = bb.getShort(4);

        Entity currentEntity = playerInfo.entity;

        if (currentEntity == null || currentEntity.id != id)
        {
            // Check the socket is allowed to update the player, or drop them...
            // TODO: check player can move player - MAJOR SECURITY RISK

            // Now update player keys belonging to player of ent
            // TODO: update player of entity - tricky
            // TODO: consider dropping this feature; we want it so admins can control/watch players etc
        }
        else
        {
            // Update current player
            playerInfo.keys = keys;
        }

        LOG.debug("Movement - ply id: {}, ent id: {}, flags: {}", playerInfo.playerId, id, keys);
    }
}
