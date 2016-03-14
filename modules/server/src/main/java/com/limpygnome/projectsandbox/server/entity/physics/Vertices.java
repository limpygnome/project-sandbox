package com.limpygnome.projectsandbox.server.entity.physics;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.map.type.tile.TileData;

/**
 * A data-structure for vertices.
 * 
 * @author limpygnome
 */
public class Vertices
{
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
     */
    public Vector2 center;
    
    /**
     * The minimum distance an ent must be within for a collision.
     */
    public float collisionRadius;
    
    
    /**
     * Builds vertices for an entity's new position.
     * 
     * @param ent 
     */
    public Vertices(Entity ent)
    {
        this(ent.positionNew, ent.rotation, ent.width, ent.height);
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
            edge = Vector2.perp(edge);
            
            // Normalise edge
            edge = Vector2.normalise(edge);
            
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
        // TODO: check this algorithm, seems hmhmmmm, especially *2,
        // should it not be the max vertex distance?

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
    
    /**
     * Builds vertices for a map tile.
     * 
     * @param map
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
    
}
