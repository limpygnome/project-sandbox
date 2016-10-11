package com.projectsandbox.components.server.util;

import java.awt.*;

/**
 *
 * @author limpygnome
 */
public class CustomMath
{
    public static final float PI_FLOAT_DOUBLE = (float) Math.PI * 2.0f;
    public static final float PI_FLOAT = (float) Math.PI;
    public static final float PI_FLOAT_HALF = (float) Math.PI / 2.0f;
    
    /**
     * Limits value inclusively within min and max.
     * 
     * @param min The min value.
     * @param max The max value.
     * @param value The value.
     * @return The limited value.
     */
    public static int limit(int min, int max, int value)
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
    
    public static float clamp(float min, float max, float value)
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

    public static float clampAngle(float radians)
    {
        while (radians < -CustomMath.PI_FLOAT) {
            radians += PI_FLOAT_DOUBLE;
        }
        while (radians >= CustomMath.PI_FLOAT) {
            radians -= PI_FLOAT_DOUBLE;
        }
        return radians;
    }
    
    public static float deg2rad(float deg)
    {
        return PI_FLOAT / 2.0f + deg * PI_FLOAT / 180.0f;
    }
    
    public static float rad2deg(float rad)
    {
        return rad * (180.0f / PI_FLOAT);
    }

    public static Color hex2Colour(String hexString)
    {
        if (hexString == null)
        {
            throw new IllegalArgumentException("Invalid hex string");
        }

        // Parse components
        int r = Integer.parseInt(hexString.substring(1, 3), 16);
        int g = Integer.parseInt(hexString.substring(3, 5), 16);
        int b = Integer.parseInt(hexString.substring(5, 7), 16);

        // Create instance
        Color result = new Color(r, g, b);
        return result;
    }

    public static String colour2Hex(Color colour)
    {
        if (colour == null)
        {
            throw new IllegalArgumentException("Null colour");
        }

        String result = String.format("#%02x%02x%02x", colour.getRed(), colour.getGreen(), colour.getBlue());
        return result;
    }

}
