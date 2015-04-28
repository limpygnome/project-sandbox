package com.limpygnome.projectsandbox.website.utils;

/**
 *
 * @author limpygnome
 */
public class CustomMath
{
    public static final float PI_FLOAT = (float) Math.PI;
    
    /**
     * Clamps value inclusively within min and max.
     * 
     * @param min The min value.
     * @param max The max value.
     * @param value The value.
     * @return The clamped value.
     */
    public static int clamp(int min, int max, int value)
    {
        if (value < min)
        {
            return min;
        }
        else if (value > max)
        {
            return max;
        }
        else
        {
            return value;
        }
    }
    
    public static float clampRepeat(float min, float max, float value)
    {
        float diff = max - min;
        float v = value - min;
        
        if (v > diff)
        {
            while (v > diff)
            {
                v -= diff;
            }
        }
        else if (v < 0)
        {
            while (v < 0)
            {
                v += diff;
            }
        }
        
        return min + v;
    }
    
    public static float deg2rad(float deg)
    {
        return PI_FLOAT / 2.0f + deg * PI_FLOAT / 180.0f;
    }
    
    public static float rad2deg(float rad)
    {
        return rad * (180.0f / PI_FLOAT);
    }
}
