package com.limpygnome.projectsandbox.server.network.packet.imp.player.individual;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.network.Socket;
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

    public short keys;

    @Override
    public void parse(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        // Check length
        if (data.length != 4)
        {
            throw new PacketParseException("Incorrect length", data);
        }

        // Parse data
        keys = bb.getShort(2);

        // Update current player
        playerInfo.keys = keys;

        //LOG.debug("Movement - ply id: {},flags: {}", playerInfo.playerId, keys);
    }

}
