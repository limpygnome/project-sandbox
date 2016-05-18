package com.limpygnome.projectsandbox.server.network.performance;

import com.limpygnome.projectsandbox.server.network.SocketEndpoint;
import com.limpygnome.projectsandbox.server.network.Socket;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by limpygnome on 18/05/16.
 */
public class TestPlayer
{
    private Socket webSocket;

    public void logic(PacketService packetService, Random random)
    {
        if (webSocket == null)
        {
            // Setup fake socket
            webSocket = new MockSocket();

            // Join server...
            ByteBuffer packet = packetJoin();
            packetService.handleInbound(webSocket, packet);
        }
        else
        {
            int randomAction = random.nextInt(8);
            ByteBuffer packet;

            switch (randomAction)
            {
                case 0:
                    // Go forwards
                    packet = packetForwards();
                    break;
                case 1:
                    // Go backwards
                    packet = packetBackwards();
                    break;
                case 2:
                    // Turn left
                    packet = packetLeft();
                    break;
                case 3:
                    // Turn right
                    packet = packetRight();
                    break;
                default:
                    // Do nothing....
                    packet = null;
                    break;
            }

            // Send data if any
            if (packet != null)
            {
                socketEndpoint.onMessage();
            }
        }
    }

    private ByteBuffer packetJoin()
    {
    }

    private ByteBuffer packetForwards()
    {
    }

    private ByteBuffer packetBackwards()
    {
    }

    private ByteBuffer packetLeft()
    {
    }

    private ByteBuffer packetRight()
    {
    }

}
