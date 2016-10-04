package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Used to contain properties belonging to an instance of {@link WorldMap}.
 */
public class GeneralMapData implements MapData
{
    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

    private String name;
    private boolean lobby;
    private boolean enabled;
    private String defaultEntityTypeName;

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
        JSONObject rawProperties = (JSONObject) root.get("properties");

        // Read and set properties
        setName((String) rawProperties.get("name"));
        setEnabled((boolean) rawProperties.get("enabled"));
        setLobby((boolean) rawProperties.get("lobby"));

        // -- Read type and fetch from ent mappings
        String entityTypeName = (String) rawProperties.get("defaultPlayerEntity");

        Class entityType = entityTypeMappingStoreService.getClassByTypeName(entityTypeName);
        if (entityType == null)
        {
            throw new RuntimeException("Unable to use '" + entityTypeName + "' as default player entity, type does not exist - map id: " + map.getMapId());
        }
        else if (!PlayerEntity.class.isAssignableFrom(entityType))
        {
            throw new RuntimeException("Default player entity must be assignable from PlayerEntity type - map id: " +
                    map.getMapId() + ", typeName: " + entityTypeName + ", class: " + entityType.getName());
        }

        setDefaultEntityTypeName(entityTypeName);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isLobby()
    {
        return lobby;
    }

    public void setLobby(boolean lobby)
    {
        this.lobby = lobby;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getDefaultEntityTypeName()
    {
        return defaultEntityTypeName;
    }

    public void setDefaultEntityTypeName(String defaultEntityType)
    {
        this.defaultEntityTypeName = defaultEntityType;
    }

    @Override
    public String toString()
    {
        return "GeneralMapData{" +
                "name='" + name + '\'' +
                ", lobby=" + lobby +
                ", enabled=" + enabled +
                ", defaultEntityTypeName='" + defaultEntityTypeName + '\'' +
                '}';
    }

}
