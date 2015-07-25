package com.limpygnome.projectsandbox.server.packets.types.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.packets.PacketParseException;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 29/04/15.
 */
public class InventoryItemSelectedInboundPacket extends InboundPacket
{
    @Override
    public void parse(Controller controller, WebSocket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        // Read raw data
        byte rawSlotId = bb.get(2);

        // Parse raw data
        short slotId = (short) rawSlotId;

        // Fetch the player's inventory
        Entity currentEntity = playerInfo.entity;

        if (currentEntity != null)
        {
            Inventory inventory = currentEntity.retrieveInventory(playerInfo);

            if (inventory != null)
            {
                // Cause invocation on item
                inventory.setSelected(slotId);
            }
        }
    }

}