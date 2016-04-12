package com.limpygnome.projectsandbox.server.entity.physics;

import com.limpygnome.projectsandbox.server.util.CustomMath;

/**
 * Vector data-structure with operations.
 * 
 * @author limpygnome
 */
public class Vector2
{
    public float x;
    public float y;
    
    public Vector2()
    {
        x = 0.0f;
        y = 0.0f;
    }
    
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(Vector2 origin, float vertX, float vertY, float rotation)
    {
        // Add origin to vertices
        this.x = vertX + origin.x;
        this.y = vertY + origin.y;
        
        // Apply rotation
        rotate(origin, rotation);
    }
    
    /**
     * Copies the values of a vector into this instance.
     * 
     * @param v The vector to copy.
     */
    public void copy(Vector2 v)
    {
        this.x = v.x;
        this.y = v.y;
    }
    
    public Vector2 clone()
    {
        return new Vector2(x, y);
    }
    
    /**
     * Offsets this instance/vector by the provided vector.
     * 
     * @param v The provided vector.
     */
    public void offset(Vector2 v)
    {
        this.x += v.x;
        this.y += v.y;
    }
    
    /**
     * Rotates this vector.
     * 
     * @param origin The origin of the rotation.
     * @param theta The rotation amount, in radians.
     */
    public void rotate(Vector2 origin, float theta)
    {
        rotate(origin.x, origin.y, theta);
    }
    
    public void rotate(float originX, float originY, float theta)
    {
        // Create sin/cos of rotation
        float angleSin = (float) Math.sin(-theta);
        float angleCos = (float) Math.cos(-theta);
        
        // Translate xy relative to origin, so that origin is 0,0
        float posX = x - originX;
        float posY = y - originY;

        // Create new co-ords around origin
        float rX = (angleCos * posX) - (angleSin * posY);
        float rY = (angleSin * posX) + (angleCos * posY);
        
        // Upadte co-ords with origin added
        this.x = rX + originX;
        this.y = rY + originY;
    }
    
    public static Vector2 add(Vector2 v, Vector2 value)
    {
        return new Vector2(v.x + value.x, v.y + value.y);
    }
    
    public static Vector2 add(Vector2 v, float value)
    {
        return new Vector2(v.x + value, v.y + value);
    }
    
    public static Vector2 add(Vector2 v, float x, float y)
    {
        return new Vector2(v.x + x, v.y + y);
    }
    
    public static Vector2 subtract(Vector2 v, Vector2 value)
    {
        return new Vector2(v.x - value.x, v.y - value.y);
    }
    
    public static Vector2 multiply(Vector2 v, float value)
    {
        return new Vector2(v.x * value, v.y * value);
    }
    
    public static Vector2 multiply(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x * b.x, a.y * b.y);
    }
    
    public static Vector2 normalise(Vector2 v)
    {
        float length = length(v);
        
        float normX;
        float normY;
        
        if (length != 0)
        {
            normX = v.x / length;
            normY = v.y / length;
        }
        else
        {
            normX = 0;
            normY = 0;
        }
        
        return new Vector2(normX, normY);
    }

    /**
     * The length of the vector.
     *
     * @param vector
     * @return
     */
    public static float length(Vector2 vector)
    {
        return (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }
    
    /**
     * Creates the perpendicular vector of the provided vector.
     * 
     * @param v The provided vector.
     * @return The provided vector, perpendicular.
     */
    public static Vector2 perp(Vector2 v)
    {
        return new Vector2(-v.y, v.x);
    }
    
    /**
     * Performs the scalar dot-product between two vectors.
     * 
     * @param a The first vector.
     * @param b The second vector.
     * @return The scalar, delta of the, dot product of the two vectors.
     */
    public static float dotProduct(Vector2 a, Vector2 b)
    {
        return (a.x * b.x) + (a.y * b.y);
    }
    
     /**
     * Creates an edge between two vectors.
     * 
     * @param v1 First vertex.
     * @param v2 Second vertex.
     * @return Creates the edge between the provided vectors.
     */
    public static Vector2 edge(Vector2 v1, Vector2 v2)
    {
        return subtract(v2, v1);
    }
    
    /**
     * The absolute distance between two entities.
     * 
     * @param a Entity A.
     * @param b Entity B.
     * @return The absolute/non-negative distance between the two entities.
     */
    public static float distance(Vector2 a, Vector2 b)
    {
        float expA = a.x - b.x;
        expA *= expA;
        
        float expB = a.y - b.y;
        expB *= expB;
        
        return Math.abs((float) Math.sqrt(expA + expB));
    }
    
    public static Vector2 vectorFromAngle(float radians, float distance)
    {
        float x = distance * (float) Math.sin(radians);
        float y = distance * (float) Math.cos(radians);
        
        return new Vector2(x, y);
    }

    public static boolean leftSide(Vector2 lineStart, Vector2 lineEnd, Vector2 vertex)
    {
        return (lineEnd.x - lineStart.x) * (vertex.y - lineStart.y) > (lineEnd.y - lineStart.y) * (vertex.x - lineStart.x);
    }

    /**
     * Calculates the rotation offset required to face a target.
     *
     * @param source The source position.
     * @param sourceRotation The rotation of the source.
     * @param target The target position.
     * @return The rotation offset to face the target.
     */
    public static float angleToFaceTarget(Vector2 source, float sourceRotation, Vector2 target)
    {
        // Calculate target rotation relative to us
        float targetRotation = CustomMath.PI_FLOAT_HALF - (float) Math.atan2(target.y - source.y, target.x - source.x);

        // Return the difference
        return CustomMath.clampAngle(targetRotation - sourceRotation);
    }

    /**
     * Inverts the value of this vector.
     *
     * @return current instance
     */
    public Vector2 invert()
    {
        x = -x;
        y = -y;

        return this;
    }

    /**
     * Sets the values of this vector, saves on creating new instances.
     *
     * @param x the x value
     * @param y the y vvalue
     * @return current instance
     */
    public Vector2 set(float x, float y)
    {
        this.x = x;
        this.y = y;

        return this;
    }

    @Override
    public String toString()
    {
        return "[x: " + x + ", y: " + y + "]";
    }
    
}
