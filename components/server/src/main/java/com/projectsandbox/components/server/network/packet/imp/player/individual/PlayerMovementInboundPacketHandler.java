package com.projectsandbox.components.server.network.packet.imp.player.individual;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.handler.AuthenticatedInboundPacketHandler;
import com.projectsandbox.components.server.network.packet.handler.InboundPacketHandler;
import com.projectsandbox.components.server.network.packet.PacketHandlerException;
import com.projectsandbox.components.server.network.packet.factory.PacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A packet sent by the client to update their movement slotState.
 * 
 * @author limpygnome
 */
@PacketHandler(mainType = 'P', subType = 'M')
public class PlayerMovementInboundPacketHandler extends AuthenticatedInboundPacketHandler
{
    private final static Logger LOG = LogManager.getLogger(PlayerMovementInboundPacketHandler.class);

    @Override
    public void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketHandlerException
    {
        // Check length
        if (data.length != 4)
        {
            throw new PacketHandlerException("Incorrect length", data);
        }

        // Parse data
        short keys = bb.getShort(2);

        // Update current player
        playerInfo.keys = keys;

        //LOG.debug("Movement - ply id: {},flags: {}", playerInfo.playerId, keys);
    }

}
