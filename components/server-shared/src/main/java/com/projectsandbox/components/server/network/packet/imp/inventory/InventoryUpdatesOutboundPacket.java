package com.projectsandbox.components.server.network.packet.imp.inventory;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;
import com.projectsandbox.components.server.network.packet.OutboundPacket;

/**
 * Created by limpygnome on 28/04/15.
 */
public class InventoryUpdatesOutboundPacket extends OutboundPacket
{
    public InventoryUpdatesOutboundPacket()
    {
        super((byte) 'I', (byte) 'U');
    }

    public void eventReset()
    {
        packetData.add((byte) 'R');
    }

    public  void eventItemSelected(Controller controller, AbstractInventoryItem item)
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
    public void eventItemCreated(Controller controller, AbstractInventoryItem item)
    {
        packetData.add((byte)'C');
        packetData.add(item.slot.idByte);
        packetData.add(item.typeId);
        packetData.addAscii(item.eventFetchItemText(controller));
    }

    /**
     * Adds state data i.e. bullets etc
     *
     * @param controller
     * @param item
     */
    public void eventItemRemoved(Controller controller, AbstractInventoryItem item)
    {
        packetData.add((byte)'D');
        packetData.add(item.slot.idByte);
    }

    public void eventItemChanged(Controller controller, AbstractInventoryItem item)
    {
        packetData.add((byte)'M');
        packetData.add(item.slot.idByte);
        packetData.addAscii(item.eventFetchItemText(controller));
    }

    public boolean isEmpty()
    {
        // Two objects for header expected
        return packetData.getSize() == 2;
    }
}
