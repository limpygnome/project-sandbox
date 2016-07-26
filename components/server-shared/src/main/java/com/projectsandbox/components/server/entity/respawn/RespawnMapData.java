package com.projectsandbox.components.server.entity.respawn;

import com.projectsandbox.components.server.world.spawn.FactionSpawns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 21/07/16.
 */
public class RespawnMapData implements Serializable
{
    private final static long serialVersionUID = 1L;
    private final static Logger LOG = LogManager.getLogger(RespawnMapData.class);

    protected List<PendingRespawn> pendingRespawnList;

    /* Faction ID -> FactionSpawns */
    protected HashMap<Short, FactionSpawns> factionSpawnsMap;

    public RespawnMapData()
    {
        this.pendingRespawnList = new LinkedList<>();
        this.factionSpawnsMap = new HashMap<>();
    }

    public synchronized void factionSpawnsAdd(Short mapId, FactionSpawns factionSpawns)
    {
        this.factionSpawnsMap.put(factionSpawns.getFactionId(), factionSpawns);
        LOG.debug("Added faction spawns - map id: {}, spawns: {}", mapId, factionSpawns);
    }

    public synchronized FactionSpawns factionSpawnsGet(short factionId)
    {
        return this.factionSpawnsMap.get(factionId);
    }

}
