package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.json.simple.JSONObject;

/**
 * Extends generic properties for instances of {@link OpenWorldMap}.
 */
public class OpenWorldMapData implements MapData
{
    private float limitWidth;
    private float limitHeight;
    private String background;

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root)
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root)
    {
        JSONObject rawProperties = (JSONObject) root.get("properties");

        if (rawProperties == null)
        {
            throw new RuntimeException("No properties section found in map file");
        }

        // Read and set custom properties for this map
        setBackground((String) rawProperties.get("background"));
        setLimitWidth((float) (double) rawProperties.get("width"));
        setLimitHeight((float) (double) rawProperties.get("height"));
    }

    public float getLimitWidth()
    {
        return limitWidth;
    }

    public void setLimitWidth(float limitWidth)
    {
        this.limitWidth = limitWidth;
    }

    public float getLimitHeight()
    {
        return limitHeight;
    }

    public void setLimitHeight(float limitHeight)
    {
        this.limitHeight = limitHeight;
    }

    public String getBackground()
    {
        return background;
    }

    public void setBackground(String background)
    {
        this.background = background;
    }

    @Override
    public String toString()
    {
        return "OpenWorldMapProperties{" +
                "limitWidth=" + limitWidth +
                ", limitHeight=" + limitHeight +
                ", background='" + background + '\'' +
                '}';
    }

}