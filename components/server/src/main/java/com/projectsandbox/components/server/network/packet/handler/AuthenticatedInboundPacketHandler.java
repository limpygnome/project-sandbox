package com.projectsandbox.components.server.network.packet.handler;

import com.projectsandbox.components.server.player.PlayerInfo;

/**
 * An abstract handler for basic authentication, with the concrete handler only serving packets for sockets with a valid
 * session or player.
 */
public abstract class AuthenticatedInboundPacketHandler extends InboundPacketHandler
{

    @Override
    public boolean isPlayerAuthenticated(PlayerInfo playerInfo)
    {
        // Check we have a player associated, simple enough...
        return playerInfo != null;
    }

}
