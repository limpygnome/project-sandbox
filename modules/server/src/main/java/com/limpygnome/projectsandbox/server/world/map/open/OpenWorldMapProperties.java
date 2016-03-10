package com.limpygnome.projectsandbox.server.world.map.open;

import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;

/**
 * Extends generic properties for instances of {@link OpenWorldMap}.
 */
public class OpenWorldMapProperties extends WorldMapProperties
{
    private float limitWidth;
    private float limitHeight;
    private String background;

    public float getLimitWidth()
    {
        return limitWidth;
    }

    public float getLimitHeight()
    {
        return limitHeight;
    }

    public String getBackground()
    {
        return background;
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
