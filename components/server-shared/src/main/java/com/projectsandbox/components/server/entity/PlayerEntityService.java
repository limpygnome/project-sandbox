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
     * Loads, otherwise creates, an entity for player.
     *
     * @param defaultMap the default map
     * @param playerInfo the player
     * @param forceCreate true = always new entity, false = look for persisted entity
     * @return an entity
     */
    public LoadOrCreateResult loadOrCreatePlayer(WorldMap defaultMap, PlayerInfo playerInfo, boolean forceCreate)
    {

        // TODO: all of this map logic is flawed / outdated, needs changing...
        try
        {
            LoadOrCreateResult result = new LoadOrCreateResult();
            GameSession gameSession = playerInfo.session;

            // Load from session if not forcibly creating new...
            if (!forceCreate)
            {
                // Fetch required data from session
                PlayerEntity entity = createPlayerFromSession(defaultMap, playerInfo, gameSession);
                Short mapId = gameSession.gameDataGetShort(PLAYERDATA_MAPID);

                // Load map
                if (mapId != null)
                {
                    WorldMap map = mapService.get(mapId);

                    if (map != null && entity != null)
                    {
                        result.loadedFromSession = true;
                        result.entity = entity;
                        result.map = map;
                    }
                }
            }

            // Create default entity if no entity yet; use provided map as default
            if (result.entity == null)
            {
                String defaultEntityTypeName = defaultMap.getProperties().getDefaultEntityTypeName();
                result.entity = (PlayerEntity) entityTypeMappingStoreService.createByTypeName(defaultEntityTypeName, null);
                result.map = defaultMap;
            }

            // Set the current player
            result.entity.setPlayer(playerInfo, 0);

            // Set flag to allow persistence of entity
            result.entity.setPersistToSession(true);

            return result;
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

            // Persist entity and mapid
            GameSession gameSession = playerInfo.session;
            gameSession.gameDataPut(PLAYERDATA_MAPID, playerEntity.map.getMapId());
            gameSession.gameDataPut(PLAYERDATA_ENTITY_KEY, playerEntity);
        }
        else if (entity != null)
        {
            LOG.debug("Player not persisted since entity is not a player entity");
        }
    }

    public static class LoadOrCreateResult
    {
        public boolean loadedFromSession;
        public WorldMap map;
        public PlayerEntity entity;

        private LoadOrCreateResult()
        {
            this.loadedFromSession = false;
            this.map = null;
            this.entity = null;
        }
    }

}
