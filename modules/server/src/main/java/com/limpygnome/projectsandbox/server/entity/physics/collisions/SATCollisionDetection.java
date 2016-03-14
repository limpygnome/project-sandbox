package com.limpygnome.projectsandbox.server.entity.physics.collisions;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Projection;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.util.CustomMath;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.type.tile.TileType;
import com.limpygnome.projectsandbox.server.world.map.type.tile.TileWorldMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Separating axis theorem (SAT) with minimum translation vector (MTV).
 * 
 * Useful resources:
 * - http://www.dyn4j.org/2010/01/sat/#sat-proj
 */
@Component
public class SATCollisionDetection implements CollisionDetection
{
    /**
     * The distance added to the penetration depth / axis overlay of MTV
     * to avoid collision detection occurring again. We basically add this
     * amount to the MTV, since without adding it, the polygons will still
     * be penetrating.
     */
    private static final float ADDED_DEPTH_MTV = 0.0001f;

    public CollisionResult collision(Entity a, Entity b)
    {
        return collision(a.cachedVertices, b.cachedVertices);
    }

    public Collection<CollisionResultMap> collisionMap(Entity entity)
    {
        WorldMap map = entity.map;

        if (map instanceof TileWorldMap)
        {
            TileWorldMap tileMap = (TileWorldMap) map;
            return collisionTileMap(tileMap, entity);
        }

        return new ArrayList<>(0);
    }


    private Collection<CollisionResultMap> collisionTileMap(TileWorldMap map, Entity entity)
    {
        // Perform collision test for each possible solid tile within range of
        // ent
        float minX = entity.positionNew.x - entity.cachedVertices.collisionRadius;
        float maxX = entity.positionNew.x + entity.cachedVertices.collisionRadius;
        float minY = entity.positionNew.y - entity.cachedVertices.collisionRadius;
        float maxY = entity.positionNew.y + entity.cachedVertices.collisionRadius;
        
        // Divide by tilesize for tile array indices
        int indexMinX = (int) Math.floor(minX / map.tileData.tileSize);
        int indexMaxX = (int) Math.ceil(maxX / map.tileData.tileSize) - 1;
        int indexMinY = (int) Math.floor(minY / map.tileData.tileSize);
        int indexMaxY = (int) Math.ceil(maxY / map.tileData.tileSize) - 1;
        
        // Clamp within bounds of array
        indexMinX = CustomMath.limit(0, map.tileData.widthTiles - 1, indexMinX);
        indexMaxX = CustomMath.limit(0, map.tileData.widthTiles - 1, indexMaxX);
        indexMinY = CustomMath.limit(0, map.tileData.heightTiles - 1, indexMinY);
        indexMaxY = CustomMath.limit(0, map.tileData.heightTiles - 1, indexMaxY);

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
                tileTypeIndex = map.tileData.tiles[y][x];
                tileType = map.tileData.tileTypes[tileTypeIndex];

                // Check if tile is eligible for collision
                if (
                        tileType.properties.solid ||
                        tileType.properties.damage != 0
                )
                {
                    // Perform collision check between tile and ent
                    result = collision(entity.cachedVertices, map.tileData.tileVertices[y][x]);
                    
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
    
    public CollisionResult collision(Vertices verticesA, Vertices verticesB)
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
    
    private CollisionResult axesOverlap(Vector2[] axes, Vertices verticesA, Vertices verticesB)
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
