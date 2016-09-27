package com.projectsandbox.components.server.map.editor.packet;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityMapData;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.map.editor.component.MapEditorComponent;
import com.projectsandbox.components.server.map.editor.entity.InvisibleMapEditorEntity;
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
    @Autowired
    private RespawnManager respawnManager;

    @Override
    public void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] rawData) throws PacketHandlerException
    {
        try
        {
            // Fetch length
            short length = bb.getShort(2);

            // Convert to string and parse as JSON...
            InputStream inputStream = new ByteArrayInputStream(rawData, 4, length);
            JSONObject data = jsonHelper.read(inputStream);

            // Fetch map and entity
            Entity entity = playerInfo.entity;
            WorldMap map = entity.map;

            // Handle type of action
            String action = (String) data.get("action");

            switch (action)
            {
                case "map-reload":

                    break;
                case "map-save":
                    break;
                case "map-clear":
                    actionClearMap(map, controller, playerInfo);
                    break;
                case "entity-select":
                    actionEntitySelect(controller, entity, data);
                    break;
                default:
                    LOG.warn("unknown map editor action - data: {}", data);
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

    private void actionReloadMap()
    {
    }

    private void actionSaveMap()
    {
    }

    private void actionClearMap(WorldMap map, Controller controller, PlayerInfo playerInfo)
    {
        // Reset entity data
        EntityMapData data = map.getEntityMapData();
        data.reset(map);

        // Respawn main player
        InvisibleMapEditorEntity entity = new InvisibleMapEditorEntity();
        entity.setPlayer(playerInfo, 0);
        respawnManager.respawn(new EntityPendingRespawn(controller, map, entity));
    }

    private void actionEntitySelect(Controller controller, Entity entity, JSONObject data)
    {
        MapEditorComponent component = getMapEditorComponent(entity);

        if (component != null && data.containsKey("typeId"))
        {
            short typeId = (short) (long) data.get("typeId");
            component.setCurrentEntityTypeId(controller, typeId);
            LOG.info("updated selected type - typeId: {}", typeId);
        }
    }

    private void actionRemoveEntity()
    {
    }

    private MapEditorComponent getMapEditorComponent(Entity entity)
    {
        MapEditorComponent component = (MapEditorComponent) entity.components.fetchComponent(MapEditorComponent.class);

        if (component == null)
        {
            LOG.warn("No map editor component found on entity - id: {}, type: {}", entity.id, entity.getClass().getName());
        }

        return component;
    }

}
