package com.limpygnome.projectsandbox.server.packets.types.session;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.java_websocket.WebSocket;

/**
 * A packet used to identify the session to which the socket belongs.
 * 
 * @author limpygnome
 */
public class SessionIdentifierInboundPacket extends InboundPacket
{
    public UUID sessionId;
    
    public SessionIdentifierInboundPacket()
    {
        this.sessionId = null;
    }

    @Override
    public void parse(Controller controller, WebSocket socket, ByteBuffer bb, byte[] data)
    {
        // TODO: upgrade to 16 bytes; at present, we want this to just be simple, but could be optimised
        
        // We're expecting a GUUID - ignore first two bytes (main/subtype) + 32 bytes
        if (data.length == (36 + 2))
        {
            try
            {
                // Read 36 bytes and convert to UUID
                String rawUuid = new String(data, 2, 36);
                
                // Parse into type, essentially validating the data
                this.sessionId = UUID.fromString(rawUuid);
            }
            catch (Exception e)
            {
                // TODO: replace with log4j
                e.printStackTrace();
            }
        }
    }   
}
