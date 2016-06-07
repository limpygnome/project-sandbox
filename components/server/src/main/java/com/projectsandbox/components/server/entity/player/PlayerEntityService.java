package com.projectsandbox.components.server.entity.player;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.shared.model.GameSession;
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

    private static final String PLAYERDATA_ENTITY_KEY = "entity";

    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

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
            }
            else
            {
                // TODO: need to sort this when multiple maps...
                playerEntity.map = worldMap;
                playerEntity.setPlayers(new PlayerInfo[]{ playerInfo });

                // Set flag to indicate this entity has been created from persistence
                playerEntity.setRespawnPersistedPlayer(true);
            }

            // Set flag to allow persistence of entity
            playerEntity.setPersistToSession(true);

            return playerEntity;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to create player entity", e);
        }
    }

    private PlayerEntity createPlayerFromSession(WorldMap worldMap, PlayerInfo playerInfo, GameSession gameSession) throws Exception
    {
        PlayerEntity playerEntity = (PlayerEntity) gameSession.gameDataGet(PLAYERDATA_ENTITY_KEY);
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

        if (entity != null && entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;

            // Check entity can be persisted
            if (playerEntity.isPersistToSession(playerInfo))
            {
                GameSession gameSession = playerInfo.session;

                // Persist entity type
                gameSession.gameDataPut(PLAYERDATA_ENTITY_KEY, playerEntity);
            }
        }
        else if (entity != null)
        {
            LOG.debug("Player not persisted since entity is not a player entity");
        }
    }

}
