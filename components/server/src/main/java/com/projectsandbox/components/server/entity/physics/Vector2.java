package com.projectsandbox.components.server.entity.physics;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.util.CustomMath;

import java.io.Serializable;

/**
 * Vector data-structure with operations.
 *
 * TODO: consider thread safety
 */
public class Vector2 implements Serializable
{
    private static final long serialVersionUID = 1L;

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
     * Rotates this vector.
     * 
     * @param origin The origin of the rotation.
     * @param theta The rotation amount, in radians.
     */
    public Vector2 rotate(Vector2 origin, float theta)
    {
        Vector2 result = rotate(origin.x, origin.y, theta);
        return result;
    }
    
    public Vector2 rotate(float originX, float originY, float theta)
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

        return this;
    }
    
    public Vector2 add(Vector2 value)
    {
        this.x += value.x;
        this.y += value.y;
        return this;
    }
    
    public Vector2 add(float value)
    {
        this.x += value;
        this.y += value;
        return this;
    }
    
    public Vector2 add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2 subtract(Vector2 value)
    {
        this.x -= value.x;
        this.y -= value.y;
        return this;
    }
    
    public Vector2 multiply(float value)
    {
        this.x *= value;
        this.y *= value;
        return this;
    }
    
    public Vector2 multiply(Vector2 value)
    {
        this.x *= value.x;
        this.y *= value.y;
        return this;
    }
    
    /**
     * @return the length of this vector
     */
    public float length()
    {
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public Vector2 normalise()
    {
        float length = length();

        if (length != 0)
        {
            this.x /= length;
            this.y /= length;
        }
        else
        {
            this.x = 0;
            this.y = 0;
        }

        return this;
    }

    public Vector2 limit(float lengthLimit)
    {
        // Limit speed/length if over
        float currentLength = length();

        if (currentLength > lengthLimit)
        {
            // Convert to unit vector
            normalise();

            // Multiply by limit
            multiply(lengthLimit);
        }

        return this;
    }
    
    /**
     * Creates the perpendicular vector of the provided vector.
     * 
     * @return the vector perpendicular to this vector
     */
    public Vector2 perp()
    {
        // For perp vector: -y, x
        float temp = x;
        x = -y;
        y = temp;
        return this;
    }
    
    /**
     * Performs the scalar dot-product between two vectors.
     * 
     * @param a the first vector.
     * @param b the second vector.
     * @return the scalar, delta of the, dot product of the two vectors.
     */
    public static float dotProduct(Vector2 a, Vector2 b)
    {
        return (a.x * b.x) + (a.y * b.y);
    }
    
     /**
     * Creates an edge between two vectors.
     * 
     * @param firstVertex
     * @param secondVertex
     * @return Creates the edge between the provided vectors.
     */
    public static Vector2 edge(Vector2 firstVertex, Vector2 secondVertex)
    {
        return new Vector2(secondVertex.x - firstVertex.x, secondVertex.y - firstVertex.y);
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

    public static Vector2 vectorFromAngle(float radians, Vector2 offset)
    {
        Vector2 offsetRotated = offset.clone().rotate(0.0f, 0.0f, radians);
        return offsetRotated;
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
     * Computes angle offset between entity and target vector.
     *
     * @param entity source entity
     * @param target the target vector
     * @return the rotation between entity's rotation and target vector
     */
    public static float computeTargetAngleOffset(Entity entity, Vector2 target)
    {
        return Vector2.angleToFaceTarget(
                entity.positionNew, entity.rotation, target
        );
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

    public boolean isOutside(float xMin, float xMax, float yMin, float yMax)
    {
        return x < xMin || x > xMax || y < yMin || y > yMax;
    }

    public Vector2 limit(float xMin, float xMax, float yMin, float yMax)
    {
        if (x < xMin)
        {
            x = xMin;
        }
        else if (x > xMax)
        {
            x = xMax;
        }

        if (y < yMin)
        {
            y = yMin;
        }
        else if (y > yMax)
        {
            y = yMax;
        }

        return this;
    }

    @Override
    public String toString()
    {
        return "{x: " + x + ", y: " + y + "}";
    }
    
}
