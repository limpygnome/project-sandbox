package com.limpygnome.projectsandbox.server.packet.imp.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.packet.InboundPacket;
import com.limpygnome.projectsandbox.server.packet.PacketParseException;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
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
