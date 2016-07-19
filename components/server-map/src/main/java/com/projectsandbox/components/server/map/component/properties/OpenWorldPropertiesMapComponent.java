package com.projectsandbox.components.server.map.component.properties;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMapProperties;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by limpygnome on 18/07/16.
 */
@Component
public class OpenWorldPropertiesMapComponent extends GeneralPropertiesMapComponent
{

    @Override
    public void load(Controller controller, JSONObject mapData, WorldMap map)
    {
        if (map instanceof OpenWorldMap)
        {
            JSONObject rawProperties = (JSONObject) mapData.get("properties");

            if (rawProperties == null)
            {
                throw new RuntimeException("No properties section found in map file");
            }

            OpenWorldMapProperties properties = (OpenWorldMapProperties) map.getProperties();

            // Read and set custom properties for this map
            properties.setBackground((String) rawProperties.get("background"));
            properties.setLimitWidth((float) (double) rawProperties.get("width"));
            properties.setLimitHeight((float) (double) rawProperties.get("height"));
        }
    }



}
