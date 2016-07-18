package com.projectsandbox.server.map.component.properties;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by limpygnome on 18/07/16.
 */
public abstract class GeneralProperties implements MapComponent
{
    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

    @Override
    public void load(Controller controller, JSONObject mapData, WorldMap map)
    {
        JSONObject rawProperties = (JSONObject) mapData.get("properties");

        WorldMapProperties properties = map.getProperties();

        // Read and set properties
        properties.setName((String) rawProperties.get("name"));
        properties.setEnabled((boolean) rawProperties.get("enabled"));
        properties.setLobby((boolean) rawProperties.get("lobby"));

        // -- Read type and fetch from ent mappings
        String entityTypeName = (String) rawProperties.get("defaultPlayerEntity");
        Class clazz = entityTypeMappingStoreService.getEntityClassByTypeName(entityTypeName);

        if (clazz == null)
        {
            throw new RuntimeException("Unable to use '" + entityTypeName + "' as default player entity, type does not exist - map id: " + map.getMapId());
        }

        properties.setDefaultEntityType(clazz);

        // Set map with properties loaded
        map.setProperties(properties);
    }

    @Override
    public void persist(Controller controller, WorldMap map)
    {
    }

}
