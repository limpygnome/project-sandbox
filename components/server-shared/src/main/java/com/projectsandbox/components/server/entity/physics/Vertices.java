package com.projectsandbox.components.server.entity.physics;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.util.CustomMath;
import com.projectsandbox.components.server.world.map.type.tile.TileData;

import java.io.Serializable;

/**
 * A data-structure for vertices.
 */
public class Vertices implements Serializable
{
    public static final long serialVersionUID = 1L;

    /**
     * The vertices of the convex shape, starting from top-left, going
     * clockwise.
     * 
     * For a rectangle:
     * TL -> TR -> BR -> BL etc
     */
    public Vector2[] vertices;
    
    /**
     * The axes for the edges of the vertices.
     */
    public Vector2[] axes;
    
    /**
     * The vector of the centre of the vertices.
     *
     * TODO: is this needed? Seems like waste...
     */
    public Vector2 center;
    
    /**
     * The minimum distance an ent must be within for a collision.
     */
    public float collisionRadius;

    private Vertices(Vector2 position, Vector2[] vertices, Vector2[] axes, float collisionRadius)
    {
        this.center = position;
        this.vertices = vertices;
        this.axes = axes;
        this.collisionRadius = collisionRadius;
    }

    public Vertices(Vector2 position, float rotation, float width, float height)
    {
        float hw = width / 2.0f;
        float hh = height / 2.0f;

        // Copy position as center
        this.center = position.clone();
        
        // Build vertices
        Vector2 topLeft = new Vector2(center, -hw, hh, rotation);
        Vector2 topRight = new Vector2(center, hw, hh, rotation);
        Vector2 bottomLeft = new Vector2(center, -hw, -hh, rotation);
        Vector2 bottomRight = new Vector2(center, hw, -hh, rotation);

        this.vertices = new Vector2[]
        {
            topLeft,
            topRight,
            bottomRight,
            bottomLeft
        };

        // Build axes
        this.axes = buildAxes();

        // Build collision radius
        this.collisionRadius = buildCollisionRadius();
    }

    /**
     * Builds vertices for an entity's new position.
     *
     * @param ent
     */
    public Vertices(Entity ent)
    {
        this(ent.positionNew, ent.rotation, ent.width, ent.height);
    }

    public Vertices(Vector2 center, Vector2[] vertices)
    {
        this.center = center.clone();

        this.vertices = vertices;

        // Build axes
        this.axes = buildAxes();

        // Build collision radius
        this.collisionRadius = buildCollisionRadius();
    }

    /**
     * Projects this set of vertices onto an axis.
     * 
     * @param axis The axis to project this set of vertices upon.
     * @return The projection.
     */
    public Projection project(Vector2 axis)
    {
        float projectedValue = Vector2.dotProduct(vertices[0], axis);
        
        float min = projectedValue;
        float max = projectedValue;
        
        for(Vector2 vertex : vertices)
        {
            // Project the vertex onto the axis
            projectedValue = Vector2.dotProduct(vertex, axis);
            
            // Check if the projection is the new min/max projected value
            if (projectedValue < min)
            {
                min = projectedValue;
            }
            else if (projectedValue > max)
            {
                max = projectedValue;
            }
        }
        
        // Return the min and max vertex projections
        return new Projection(min, max);
    }
    
    /**
     * Retrieves the axes for the vertices.
     * 
     * @return The axes.
     */
    private Vector2[] buildAxes()
    {
        Vector2[] axes = new Vector2[vertices.length];
        
        Vector2 vertex;
        Vector2 vertexNext;
        Vector2 edge;
        
        for (int i = 0; i < vertices.length; i++)
        {
            // Get current vertex and form an edge by joining it with the
            // next vertex
            vertex = vertices[i];
            vertexNext = vertices[i + 1 == vertices.length ? 0 : i + 1];
            
            // Create the edge
            edge = Vector2.edge(vertex, vertexNext);
            
            // Set edge to be perpendicular
            edge.perp();

            // Normalise edge
            edge.normalise();

            // Set axis to edge
            axes[i] = edge;
        }

        return axes;
    }
    
    /**
     * Builds the minimum distance, from the center position, to which a
     * collision can occur with another entity.
     * 
     * @return The minimum distance.
     */
    public float buildCollisionRadius()
    {
        float max = 0.0f;
        float v;

        for (int i = 0; i < vertices.length; i++)
        {
            v = Vector2.distance(center, vertices[i]);

            if (v > max)
            {
                max = v;
            }
        }

        // Must be within double of distance from center to furthest vertex
        return max;
    }

    /**
     * Creates a clone of the current instance.
     *
     * @return a clone of the current instance
     */
    public Vertices clone()
    {
        // Clone data
        Vector2 positionClone = center.clone();
        Vector2[] verticesClone = Vector2.cloneArray(vertices);
        Vector2[] axesClone = Vector2.cloneArray(axes);

        // Create instance
        Vertices vertices = new Vertices(positionClone, verticesClone, axesClone, collisionRadius);
        return vertices;
    }

    /**
     * Offsets vertices by provided vector/offset.
     *
     * This will offset vertices, center and axes.
     *
     * @param offset the offset
     * @return the current instance
     */
    public Vertices offset(Vector2 offset)
    {
        // Offset position
        center.add(offset);

        // Offset vertices
        for (Vector2 vertex : vertices)
        {
            vertex.add(offset);
        }

        return this;
    }

    public Vertices rotate(float rotation)
    {
        // Rotate vertices
        for (Vector2 vertex : vertices)
        {
            vertex.rotate(center.x, center.y, rotation);
        }

        // Rebuild axes
        this.axes = buildAxes();

        return this;
    }

    /**
     * Builds vertices for a map tile.
     * 
     * @param tileData
     * @param tileX
     * @param tileY
     * @return 
     */
    public static Vertices buildTileVertices(TileData tileData, int tileX, int tileY)
    {
        // Calculate x and y - y must be inverted!
        float x = (tileX * tileData.tileSize) + tileData.tileSizeHalf;
        float y = (tileY * tileData.tileSize) + tileData.tileSizeHalf;
        
        Vector2 position = new Vector2(x, y);
        
        return new Vertices(position, 0.0f, tileData.tileSize, tileData.tileSize);
    }

    /**
     *
     * @param radiusX the radius of the X axis
     * @param radiusY the radius of the Y axis
     * @param numberOfVertices the total number of vertices
     * @return
     */
    public static Vertices buildEllipsis(float radiusX, float radiusY, int numberOfVertices)
    {
        // Compute vertices
        float angleSeparation = CustomMath.PI_FLOAT_DOUBLE / numberOfVertices;

        float angle;
        Vector2 vertex;
        Vector2[] vertices = new Vector2[numberOfVertices];

        for (int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++)
        {
            // Calculate point of ellipsis
            angle = angleSeparation * vertexIndex;
            vertex = pointOfEllipsis(radiusX, radiusY, angle);

            vertices[vertexIndex] = vertex;
        }

        // Create Vertices instance
        Vertices instance = new Vertices(new Vector2(), vertices);
        return instance;
    }

    private static Vector2 pointOfEllipsis(float radiusX, float radiusY, float radians)
    {
        Vector2 point = new Vector2(
                radiusX * (float) Math.cos(radians),
                radiusY * (float) Math.sin(radians)
        );
        return point;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Append vertices
        sb.append("[").append(vertices.length).append(" vertices:\n");

        for (int i = 0; i < vertices.length; i++)
        {
            sb      .append("\tv").append(i).append(" - ").append(vertices[i])
                    .append("\n");
        }

        // Append center
        sb.append("center: ").append(center).append("\n");

        // Append collision radius
        sb.append("collision radius: ").append(collisionRadius).append("\n");

        // Remove tail
        sb.append("]");

        return sb.toString();
    }

}
