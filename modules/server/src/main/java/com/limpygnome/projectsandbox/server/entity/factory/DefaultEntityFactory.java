package com.limpygnome.projectsandbox.server.entity.factory;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * The default implementation reads the following map property to spawn an entity of a type:
 * 'defaultEntity'
 *
 * This will attempt to load the previous entity and inventory
 */
@Component
public class DefaultEntityFactory implements EntityFactory
{
    private final static Logger LOG = LogManager.getLogger(DefaultEntityFactory.class);

    private static final String PLAYERDATA_INVENTORY_KEY = "player_persisted_inventory";
    private static final String PLAYERDATA_ENT_TYPE_ID_KEY = "player_persisted_entity_type";

    private WorldMap worldMap;

    public DefaultEntityFactory(WorldMap worldMap)
    {
        this.worldMap = worldMap;
    }

    @Override
    public PlayerEntity createPlayer(PlayerInfo playerInfo)
    {
        try
        {
            GameSession gameSession = playerInfo.session;
            PlayerEntity playerEntity;

            // Attempt to load from session
            playerEntity = createPlayerFromSession(playerInfo, gameSession);

            if (playerEntity == null)
            {
                // Create default entity for map
                Class clazz = worldMap.getProperties().getDefaultEntityType();
                playerEntity = createFromClass(clazz, playerInfo);
            }

            return playerEntity;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to create player entity", e);
        }
    }

    private PlayerEntity createPlayerFromSession(PlayerInfo playerInfo, GameSession gameSession) throws Exception
    {
        PlayerEntity playerEntity = null;

        Short entityTypeId = gameSession.gameDataGetShort(PLAYERDATA_ENT_TYPE_ID_KEY);

        if (entityTypeId != null)
        {
            Class clazz = worldMap.entityManager.entTypeMappingStoreService.getEntityClassByTypeId(entityTypeId);

            if (!clazz.isAssignableFrom(PlayerEntity.class))
            {
                LOG.warn("Attempted to spawn player from non player entity class - type id: {}, actual class: {}", entityTypeId, clazz.getName());
            }
            else
            {
                // Create instance
                playerEntity = createFromClass(clazz, playerInfo);

                // Attempt to load inventory
                Inventory inventory = (Inventory) gameSession.gameDataGet(PLAYERDATA_INVENTORY_KEY);

                if (inventory != null)
                {
                    playerEntity.setInventory(0, inventory);
                }
            }
        }

        return playerEntity;
    }

    private PlayerEntity createFromClass(Class clazz, PlayerInfo playerInfo) throws Exception
    {
        PlayerEntity playerEntity = (PlayerEntity) clazz.getConstructor(WorldMap.class, PlayerInfo[].class).newInstance(worldMap, new PlayerInfo[]{ playerInfo });
        return playerEntity;
    }

    @Override
    public void persistPlayer(PlayerInfo playerInfo)
    {
    }

}
