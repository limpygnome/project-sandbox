package com.projectsandbox.components.server.map.editor.packet;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityMapData;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.PacketHandlerException;
import com.projectsandbox.components.server.network.packet.factory.PacketHandler;
import com.projectsandbox.components.server.network.packet.handler.AuthenticatedInboundPacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.util.JsonHelper;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.shared.model.Role;
import com.projectsandbox.components.shared.model.Roles;
import com.projectsandbox.components.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Handles all map editing packet data.
 */
@PacketHandler(mainType = 'M', subType = 'E')
public class MapEditorInboundPacket extends AuthenticatedInboundPacketHandler
{
    private final static Logger LOG = LogManager.getLogger(MapEditorInboundPacket.class);

    @Autowired
    private JsonHelper jsonHelper;

    @Override
    public void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] rawData) throws PacketHandlerException
    {
        try
        {
            // Convert to string and parse as JSON...
            InputStream inputStream = new ByteArrayInputStream(rawData);
            JSONObject data = jsonHelper.read(inputStream);

            // Fetch map and entity
            Entity entity = playerInfo.entity;
            WorldMap map = entity.map;

            // Handle type of action
            String action = (String) data.get("action");

            switch (action)
            {
                case "map-reset":

                    break;
                case "map-save":
                    break;
                case "map-clear":
                    actionClearMap(map);
                    break;
                case "entity-add":

                    break;
                case "entity-remove":

                    break;
            }
        }
        catch (Exception e)
        {
            LOG.error("Failed to parse map data", e);
        }
    }

    @Override
    public boolean isPlayerAuthenticated(PlayerInfo playerInfo)
    {
        boolean auth = false;

        if (super.isPlayerAuthenticated(playerInfo))
        {
            // Check player is allowed to edit maps...
            User user = playerInfo.session.getUser();

            if (user != null)
            {
                Roles roles = user.getRoles();
                auth = roles.contains(Role.ADMINISTRATOR);
            }
        }

        return auth;
    }

    private void actionResetMap()
    {
    }

    private void actionSaveMap()
    {
    }

    private void actionClearMap(WorldMap map)
    {
        // Reset entity data
        EntityMapData data = map.getEntityMapData();
        data.reset(map);

        // TODO: reset spawns too?
    }

    private void actionAddEntity()
    {
    }

    private void actionRemoveEntity()
    {
    }

}
