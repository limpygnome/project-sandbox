package com.limpygnome.projectsandbox.server.packets.types.players.chat;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.packets.PacketParseException;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 09/06/15.
 */
public class PlayerChatInboundPacket extends InboundPacket
{
    public PlayerInfo playerInfo;
    public String message;

    @Override
    public void parse(Controller controller, WebSocket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        if (data.length < 3)
        {
            throw new PacketParseException("Incorrect length", data);
        }

        this.playerInfo = playerInfo;
        this.message = new String(data, 0, data.length - 1);
    }
}
