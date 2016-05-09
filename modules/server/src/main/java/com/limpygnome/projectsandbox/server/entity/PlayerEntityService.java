package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service used to create and persist the entity of players.
 */
@Service
public class PlayerEntityService
{
    private final static Logger LOG = LogManager.getLogger(PlayerEntityService.class);

    private static final String PLAYERDATA_INVENTORY_KEY = "player_persisted_inventory";
    private static final String PLAYERDATA_ENT_TYPE_ID_KEY = "player_persisted_entity_type";

    @Autowired
    private EntTypeMappingStoreService entTypeMappingStoreService;

    public PlayerEntity createPlayer(WorldMap worldMap, PlayerInfo playerInfo)
    {
        try
        {
            GameSession gameSession = playerInfo.session;
            PlayerEntity playerEntity;

            // Attempt to load from session
            playerEntity = createPlayerFromSession(worldMap, playerInfo, gameSession);

            if (playerEntity == null)
            {
                // Create default entity for map
                Class clazz = worldMap.getProperties().getDefaultEntityType();
                playerEntity = createFromClass(worldMap, clazz, playerInfo);

                // Set flag to allow persistence of entity
                playerEntity.setPersistToSession(true);
            }

            return playerEntity;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to create player entity", e);
        }
    }

    private PlayerEntity createPlayerFromSession(WorldMap worldMap, PlayerInfo playerInfo, GameSession gameSession) throws Exception
    {
        PlayerEntity playerEntity = null;

        Short entityTypeId = gameSession.gameDataGetShort(PLAYERDATA_ENT_TYPE_ID_KEY);

        if (entityTypeId != null && entityTypeId > 0)
        {
            Class clazz = entTypeMappingStoreService.getEntityClassByTypeId(entityTypeId);
            boolean isPlayerEntityInstance = PlayerEntity.class.isAssignableFrom(clazz);

            if (clazz == null || !isPlayerEntityInstance)
            {
                LOG.warn("Attempted to spawn player from non player entity class - type id: {}, actual class: {}", entityTypeId, clazz != null ? clazz.getName() : null);
            }
            else
            {
                // Create instance
                playerEntity = createFromClass(worldMap, clazz, playerInfo);

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

    private PlayerEntity createFromClass(WorldMap worldMap, Class clazz, PlayerInfo playerInfo) throws Exception
    {
        PlayerEntity playerEntity = (PlayerEntity) clazz.getConstructor(WorldMap.class, PlayerInfo[].class).newInstance(worldMap, new PlayerInfo[]{ playerInfo });
        return playerEntity;
    }

    public void persistPlayer(PlayerInfo playerInfo)
    {
        // Fetch the player's current entity
        Entity entity = playerInfo.entity;

        if (entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;

            // Check entity can be persisted
            if (playerEntity.isPersistToSession(playerInfo))
            {
                GameSession gameSession = playerInfo.session;

                // Persist entity type
                gameSession.gameDataPut(PLAYERDATA_ENT_TYPE_ID_KEY, playerEntity.entityType);

                // Persist inventory
                Inventory inventory = playerEntity.getInventory();
                gameSession.gameDataPut(PLAYERDATA_INVENTORY_KEY, inventory);
            }
        }
    }

}
