package com.limpygnome.projectsandbox.utils;

/**
 *
 * @author limpygnome
 */
public class CustomMath
{
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
}
