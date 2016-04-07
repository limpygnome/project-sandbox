package com.limpygnome.projectsandbox.server.network.packet.imp.session;

import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;

/**
 * Created by limpygnome on 28/07/15.
 */
public class SessionErrorCodeOutboundPacket extends OutboundPacket
{
    public enum ErrorCodeType
    {
        SESSION_NOT_FOUND(1)
        ;
        public final int ERROR_CODE;
        ErrorCodeType(int ERROR_CODE)
        {
            this.ERROR_CODE = ERROR_CODE;
        }
    }

    public SessionErrorCodeOutboundPacket(ErrorCodeType errorCodeType)
    {
        super((byte) 'S', (byte) 'E');

        packetData.add(errorCodeType.ERROR_CODE);
    }

}
