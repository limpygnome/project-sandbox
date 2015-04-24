package com.limpygnome.projectsandbox.packets.outbound.inventory;

import com.limpygnome.projectsandbox.inventory.Inventory;
import com.limpygnome.projectsandbox.inventory.InventoryItem;
import com.limpygnome.projectsandbox.packets.OutboundPacket;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
public class InventoryCreatePacket extends OutboundPacket
{
    public InventoryCreatePacket()
    {
        super((byte)'I', (byte)'C');
    }
    
    public void build(Inventory inventory) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        // Add items
        byte i = 0;
        for (InventoryItem item : inventory.items)
        {
            // slot , typeid , customdata for id
            packetData.add(i++);
            packetData.add(item.getTypeId());
            item.writeInventoryCreatePacket(packetData);
        }
        
        // Write packet data
        write(packetData);
    }
}
