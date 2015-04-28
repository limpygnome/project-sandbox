package com.limpygnome.projectsandbox.server.packets.outbound.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.InventoryItem;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by limpygnome on 28/04/15.
 */
public class InventoryUpdatesPacket extends OutboundPacket
{
    private LinkedList<Object> packetData;
    public InventoryUpdatesPacket()
    {
        super((byte) 'I', (byte) 'U');

        this.packetData = new LinkedList<>();;
    }

    public void eventReset()
    {
        packetData.add((byte)'R');
    }

    public  void eventSelected(Controller controller, InventoryItem item)
    {
    }

    public void eventItemCreated(Controller controller, InventoryItem item)
    {
    }

    public void eventItemRemoved(Controller controller, InventoryItem item)
    {
    }

    public void eventItemChanged(Controller controller, InventoryItem item)
    {
    }

    public void finalize() throws IOException
    {
        write(packetData);
        packetData = null;
    }
}
