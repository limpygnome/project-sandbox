package com.limpygnome.projectsandbox.server.ents.physics.collisions;

import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.Projection;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.Vertices;
import com.limpygnome.projectsandbox.server.utils.CustomMath;
import com.limpygnome.projectsandbox.server.world.Map;
import com.limpygnome.projectsandbox.server.world.TileType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Separating axis theorem (SAT) with minimum translation vector (MTV).
 * 
 * Useful resources:
 * - http://www.dyn4j.org/2010/01/sat/#sat-proj
 * 
 * @author limpygnome
 */
public class SAT
{
    /**
     * The distance added to the penetration depth / axis overlay of MTV
     * to avoid collision detection occurring again. We basically add this
     * amount to the MTV, since without adding it, the polygons will still
     * be penetrating.
     */
    private static final float ADDED_DEPTH_MTV = 0.0001f;
    
    public static CollisionResult collision(Entity a, Entity b)
    {
        return collision(a.cachedVertices, b.cachedVertices);
    }
    
    public static Collection<CollisionResultMap> collisionMap(Map map, Entity ent)
    {
        // Perform collision test for each possible solid tile within range of
        // ent
        float minX = ent.positionNew.x - ent.cachedVertices.collisionRadius;
        float maxX = ent.positionNew.x + ent.cachedVertices.collisionRadius;
        float minY = ent.positionNew.y - ent.cachedVertices.collisionRadius;
        float maxY = ent.positionNew.y + ent.cachedVertices.collisionRadius;
        
        // Divide by tilesize for tile array indices
        float ts = (float) map.tileSize;
        
        int indexMinX = (int) Math.floor(minX / ts);
        int indexMaxX = (int) Math.ceil(maxX / ts) - 1;
        int indexMinY = (int) Math.floor(minY / ts);
        int indexMaxY = (int) Math.ceil(maxY / ts) - 1;
        
        // Clamp within bounds of array
        indexMinX = CustomMath.limit(0, map.width - 1, indexMinX);
        indexMaxX = CustomMath.limit(0, map.width - 1, indexMaxX);
        indexMinY = CustomMath.limit(0, map.height - 1, indexMinY);
        indexMaxY = CustomMath.limit(0, map.height - 1, indexMaxY);

        // Fetch tiles within range and test for collision with solid tiles
        short tileTypeIndex;
        TileType tileType;
        CollisionResult result;
        
        // Build array for results
        int maxCollisions = (indexMaxY - indexMinY) * (indexMaxX - indexMinX);
        ArrayList<CollisionResultMap> collisions = new ArrayList<>(maxCollisions);
        
        // Test tiles
        for (int y = indexMinY; y <= indexMaxY; y++)
        {
            for (int x = indexMinX; x <= indexMaxX; x++)
            {
                // Fetch tile
                tileTypeIndex = map.tiles[y][x];
                tileType = map.tileTypes[tileTypeIndex];

                // Check if tile is eligible for collision
                if (
                        tileType.properties.solid ||
                        tileType.properties.damage != 0
                )
                {
                    // Perform collision check between tile and ent
                    result = SAT.collision(ent.cachedVertices, map.tileVertices[y][x]);
                    
                    // Check if a collision occurred
                    if (result.collision)
                    {
                        collisions.add(new CollisionResultMap(result, x, y, tileType));
                    }
                }
            }
        }
        
        return collisions;
    }
    
    public static CollisionResult collision(Vertices verticesA, Vertices verticesB)
    {
        float dist = Vector2.distance(verticesA.center, verticesB.center);

        // Check ents are at least within each other's combined radius's
        if (dist > verticesA.collisionRadius + verticesB.collisionRadius)
        {
            // Not possible...
            return new CollisionResult(false, null, null, 0.0f);
        }
        
        // Check for overlap on axes of A
        CollisionResult mtvA = axesOverlap(verticesA.axes, verticesA, verticesB);
        if (mtvA == null)
        {
            return null;
        }
        
        // Check for overlap on axes of B
        CollisionResult mtvB = axesOverlap(verticesB.axes, verticesA, verticesB);
        if (mtvB == null)
        {
            return null;
        }
        
        return mtvA.depth < mtvB.depth ? mtvA : mtvB;
    }
    
    private static CollisionResult axesOverlap(Vector2[] axes, Vertices verticesA, Vertices verticesB)
    {
        Projection pA;
        Projection pB;
        
        Vector2 smallestAxis = null;
        float smallestOverlap = 0.0f;
        float overlap;
        
        for(Vector2 axis : axes)
        {
            // Project vertices onto axis
            pA = verticesA.project(axis);
            pB = verticesB.project(axis);
            
            // Get the overlap
            overlap = pA.overlap(pB);
            
            // Check for overlap of projected min/max's
            if (overlap > 0)
            {
                // No overlap - collision not possible!
                return new CollisionResult(false, null, null, 0.0f);
            }
            
            // Check if it's the smallest overlap for the MTV
            overlap = Math.abs(overlap);
            
            if (smallestAxis == null || overlap < smallestOverlap)
            {
                smallestOverlap = overlap;
                smallestAxis = axis;
            }
        }
        
        // Check we found an axis
        if (smallestAxis != null)
        {
            // Check if to invert axis
            Vector2 negativeAxisCheck = Vector2.subtract(verticesA.center, verticesB.center);
            if (Vector2.dotProduct(negativeAxisCheck, smallestAxis) < 0)
            {
                smallestAxis = Vector2.multiply(smallestAxis, -1.0f);
            }
        }
        
        // Create minimal translation vector (MTV)
        Vector2 mtv = Vector2.multiply(
                smallestAxis,
                smallestOverlap + ADDED_DEPTH_MTV
        );
        
        // Return the result from the collision
        return new CollisionResult(true, mtv, smallestAxis, smallestOverlap);
    }
    
}
