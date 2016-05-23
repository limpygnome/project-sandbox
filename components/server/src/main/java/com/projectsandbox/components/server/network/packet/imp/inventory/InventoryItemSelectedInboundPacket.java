package com.projectsandbox.components.server.network.packet.imp.inventory;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.PlayerEntity;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.InboundPacket;
import com.projectsandbox.components.server.network.packet.PacketParseException;
import com.projectsandbox.components.server.player.PlayerInfo;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 29/04/15.
 */
public class InventoryItemSelectedInboundPacket extends InboundPacket
{
    @Override
    public void parse(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        // Read raw data
        byte rawSlotId = bb.get(2);

        // Parse raw data
        short slotId = (short) rawSlotId;

        // Fetch the player's inventory
        Entity entity = playerInfo.entity;

        if (entity != null && entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            Inventory inventory = playerEntity.getInventory(playerInfo);

            if (inventory != null)
            {
                // Cause invocation on item
                inventory.setSelected(slotId);
            }
        }
    }

}
