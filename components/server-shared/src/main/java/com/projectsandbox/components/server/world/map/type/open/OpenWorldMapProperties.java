package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.world.map.WorldMapProperties;

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
