package com.projectsandbox.components.server.entity.respawn;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.respawn.pending.PendingRespawn;
import com.projectsandbox.components.server.util.CustomMath;
import com.projectsandbox.components.server.world.map.mapdata.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Faction;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by limpygnome on 21/07/16.
 */
@Component
@Scope(value = "prototype")
public class RespawnMapData implements MapData
{
    private final static Logger LOG = LogManager.getLogger(RespawnMapData.class);

    @Autowired
    private SpawnParserHelper spawnParserHelper;

    protected List<PendingRespawn> pendingRespawnList;

    /* Faction ID -> Faction */
    protected HashMap<Short, Faction> factionSpawnsMap;

    public RespawnMapData()
    {
        this.pendingRespawnList = new LinkedList<>();
        this.factionSpawnsMap = new HashMap<>();
    }

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root)
    {
        JSONArray factionSpawnsData = new JSONArray();
        JSONObject factionSpawnData;

        for (Map.Entry<Short, Faction> kv : factionSpawnsMap.entrySet())
        {
            // Only serialize factions with spawns...
            if (kv.getValue().hasSpawns())
            {
                factionSpawnData = serializeFactionSpawn(kv.getValue());
                factionSpawnsData.add(factionSpawnData);
            }
        }

        // Attach factions spawns to parent
        root.put("factionSpawns", factionSpawnsData);
    }

    public JSONObject serializeFactionSpawn(Faction factionSpawn)
    {
        JSONObject factionData = new JSONObject();
        JSONArray spawnsData = new JSONArray();
        JSONObject spawnData;

        for (Spawn spawn : factionSpawn.getSpawns())
        {
            spawnData = spawnParserHelper.serialize(spawn);
            spawnsData.add(spawnData);
        }

        // Attach settings to faction
        factionData.put("id", factionSpawn.getFactionId());
        factionData.put("spawns", spawnsData);

        String hex = CustomMath.colour2Hex(factionSpawn.getColour());
        factionData.put("colour", hex);

        return factionData;
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root)
    {
        // Parse data for spawns
        JSONArray factionSpawnsData = (JSONArray) root.get("factions");
        JSONObject factionSpawnData;

        for (Object factionData : factionSpawnsData)
        {
            factionSpawnData = (JSONObject) factionData;
            deserializeFactionSpawn(map, factionSpawnData);
        }
    }

    private void deserializeFactionSpawn(WorldMap map, JSONObject factionData)
    {
        short factionId = (short) (long) factionData.get("id");
        String rawColour = (String) factionData.get("colour");
        Color colour = CustomMath.hex2Colour(rawColour);

        Faction faction = new Faction(factionId, colour);

        // Parse spawns
        JSONArray spawnsData = (JSONArray) factionData.get("spawns");

        if (spawnsData != null)
        {
            Spawn spawn;
            for (Object spawnData : spawnsData)
            {
                spawn = spawnParserHelper.deserialize((JSONObject) spawnData);
                faction.addSpawn(spawn);
            }
        }

        this.factionSpawnsMap.put(faction.getFactionId(), faction);
        LOG.debug("Added faction spawns - map id: {}, spawns: {}", map.getMapId(), faction);
    }

    public synchronized Faction factionSpawnsGet(short factionId)
    {
        return this.factionSpawnsMap.get(factionId);
    }

    public synchronized Map<Short, Faction> getFactionSpawns()
    {
        Map<Short, Faction> factionSpawnsMap = Collections.unmodifiableMap(this.factionSpawnsMap);
        return factionSpawnsMap;
    }

}
