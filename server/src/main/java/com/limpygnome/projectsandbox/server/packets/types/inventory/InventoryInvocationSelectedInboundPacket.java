package com.limpygnome.projectsandbox.server.packets.types.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Allows an item to be invoked.
 *
 * Created by limpygnome on 29/04/15.
 */
public class InventoryInvocationSelectedInboundPacket extends InboundPacket
{
    @Override
    public void parse(Controller controller, WebSocket socket, ByteBuffer bb, byte[] data)
    {
        // Read raw data
        byte rawSlotId = bb.get(0);
        byte rawKeyDown = bb.get(1);

        // Parse raw data
        short slotId = (short) rawSlotId;
        boolean keyDown = rawKeyDown != 0;

        // Fetch the player's inventory
        PlayerInfo playerInfo = fetchPlayer(controller, socket);
        Inventory inventory = playerInfo.entity.retrieve(playerInfo);

        if (inventory != null)
        {
            // Cause invocation on item
            inventory.invokeItem(controller, slotId, keyDown);
        }
    }
}
