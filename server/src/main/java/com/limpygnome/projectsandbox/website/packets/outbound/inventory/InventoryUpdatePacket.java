package com.limpygnome.projectsandbox.website.packets.outbound.inventory;

import com.limpygnome.projectsandbox.website.inventory.InventoryItem;
import com.limpygnome.projectsandbox.website.packets.OutboundPacket;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
public class InventoryUpdatePacket extends OutboundPacket
{
    public InventoryUpdatePacket()
    {
        super((byte)'I', (byte)'U');
    }
    
    public void build(InventoryItem... items) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        // Write update data
        for (InventoryItem item : items)
        {
            item.writeInventoryUpdatePacket(packetData);
        }
        
        // Write packet data
        write(packetData);
    }
}
