package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.world.map.MapService;
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
    private static final String PLAYERDATA_MAPID = "entity_mapid";

    @Autowired
    private MapService mapService;
    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

    /**
     * Creates a new player.
     *
     * @param worldMap the current map
     * @param playerInfo the player
     * @param forceCreate true = always new entity, false = look for persisted entity
     * @return an entity
     */
    public PlayerEntity createPlayer(WorldMap worldMap, PlayerInfo playerInfo, boolean forceCreate)
    {
        // TODO: all of this map logic is flawed / outdated, needs changing...
        try
        {
            GameSession gameSession = playerInfo.session;
            PlayerEntity playerEntity;

            if (!forceCreate)
            {
                // Attempt to load from session
                playerEntity = createPlayerFromSession(worldMap, playerInfo, gameSession);

                if (playerEntity != null)
                {
                    // Fetch the map to which the entity belonged
                    Short mapId = gameSession.gameDataGetShort(PLAYERDATA_MAPID);
                    WorldMap entityMap;

                    if (mapId != null)
                    {
                        entityMap = mapService.get(mapId);
                    }
                    else
                    {
                        entityMap = null;
                    }

                    // Setup entity if we found the map
                    if (entityMap != null)
                    {
                        // Set map
                        playerEntity.map = entityMap;

                        // Set flag to indicate this entity has been created from persistence
                        playerEntity.setRespawnPersistedPlayer(true);
                    }
                    else
                    {
                        playerEntity = null;
                    }
                }
            }
            else
            {
                playerEntity = null;
            }

            // Create default entity if no entity yet
            if (playerEntity == null)
            {
                String defaultEntityTypeName = worldMap.getProperties().getDefaultEntityTypeName();
                playerEntity = (PlayerEntity) entityTypeMappingStoreService.createByTypeName(defaultEntityTypeName, null);
            }

            // Set the current player
            playerEntity.setPlayer(playerInfo, 0);

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

                // Persist entity and mapid
                gameSession.gameDataPut(PLAYERDATA_MAPID, playerEntity.map.getMapId());
                gameSession.gameDataPut(PLAYERDATA_ENTITY_KEY, playerEntity);
            }
        }
        else if (entity != null)
        {
            LOG.debug("Player not persisted since entity is not a player entity");
        }
    }

}
