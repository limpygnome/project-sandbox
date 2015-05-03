package com.limpygnome.projectsandbox.server.packets.types.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.InventoryItem;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.utils.ByteHelper;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by limpygnome on 28/04/15.
 */
public class InventoryUpdatesOutboundPacket extends OutboundPacket
{
    private LinkedList<Object> packetData;

    public InventoryUpdatesOutboundPacket()
    {
        super((byte) 'I', (byte) 'U');

        this.packetData = new LinkedList<>();;
    }

    public void eventReset()
    {
        packetData.add((byte) 'R');
    }

    public  void eventItemSelected(Controller controller, InventoryItem item)
    {
        if (item != null)
        {
            packetData.add((byte)'S');
            packetData.add(item.slot.idByte);
        }
        else
        {
            // No item selected / no items in inventory
            packetData.add((byte)'N');
        }
    }

    /**
     * Adds creation data i.e. type / initial setup params
     *
     * @param controller
     * @param item
     */
    public void eventItemCreated(Controller controller, InventoryItem item)
    {
        packetData.add((byte)'C');
        packetData.add(item.slot.idByte);
        packetData.add(item.getTypeId());
        item.eventInventoryWritePacketCreated(controller, packetData);
    }

    /**
     * Adds state data i.e. bullets etc
     *
     * @param controller
     * @param item
     */
    public void eventItemRemoved(Controller controller, InventoryItem item)
    {
        packetData.add((byte)'R');
        packetData.add(item.slot.idByte);
        item.eventInventoryWritePacketRemoved(controller, packetData);
    }

    public void eventItemChanged(Controller controller, InventoryItem item)
    {
        packetData.add((byte)'M');
        packetData.add(item.slot.idByte);
        item.eventInventoryWritePacketChanged(controller, packetData);
    }

    public void build() throws IOException
    {
        write(packetData);

        packetData = null;
    }

    public boolean isEmpty()
    {
        return packetData.isEmpty();
    }
}
