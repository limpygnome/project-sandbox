package com.limpygnome.projectsandbox.server.entity.physics.casting;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.physics.casting.victims.EntityCastVictim;
import com.limpygnome.projectsandbox.server.entity.physics.casting.victims.MapCastVictim;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.util.CustomMath;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 *
 * @author limpygnome
 */
public class Casting
{
    private final static Logger LOG = LogManager.getLogger(Casting.class);

    private static final float RADIANS_90_DEGREES = 1.57079633f;

    public static CastingResult cast(Controller controller, Entity origin, float radians, float maxDistance)
    {
        // Create line from origin
        Vector2 lineStart = new Vector2(origin.positionNew.x, origin.positionNew.y);
        Vector2 lineEnd = Vector2.vectorFromAngle(radians, maxDistance);
        lineEnd.offset(lineStart);

        // Create perpendicular line from origin
        Vector2 linePerpStart = lineStart.clone();
        Vector2 linePerpEnd = Vector2.vectorFromAngle(radians + RADIANS_90_DEGREES, maxDistance);
        linePerpEnd.offset(linePerpStart);

        // Find closest entity intersection
        CastingResult resultEnt = castEnts(controller, origin, lineStart, lineEnd, linePerpStart, linePerpEnd, maxDistance);

        // Find closest map intersection
        CastingResult resultMap = castMap(controller, origin, lineStart, lineEnd, linePerpStart, linePerpEnd, maxDistance);

        // Prepare the result from our tests
        CastingResult result = null;

        if (resultEnt != null && resultMap != null)
        {
            if (resultEnt.distance < resultMap.distance)
            {
                result = resultEnt;
            }
            else
            {
                result = resultMap;
            }
        }
        else if (resultEnt != null)
        {
            result = resultEnt;
        }
        else if (resultMap != null)
        {
            result = resultMap;
        }

        // Set collision flag, or an empty new instance if no result (false flag by default)
        if (result != null)
        {
            result.collision = true;
        }
        else
        {
            result = new CastingResult();
            result.x = lineEnd.x;
            result.y = lineEnd.y;
        }

        return result;
    }

    private static CastingResult castEnts(Controller controller, Entity origin, Vector2 lineStart, Vector2 lineEnd,
                                          Vector2 linePerpStart, Vector2 linePerpEnd, float maxDistance)
    {
        // Iterate and find each entity within the radius of being hit
        List<ProximityResult> possibleEnts = DefaultProximity.nearbyEnts(controller, origin, maxDistance, true, true);

        // 1. Ignore all the ents which are on the other side of the perp of origin i.e. complete opposite direction
        // 2. Iterate each vertex; a collision has occurred when a vertex is on the different side of the line to
        //    another vertex
        // 3. Perform line intersection and store the closest; this will be where the bullet hits
        boolean collision;

        CastingResult result;
        CastingResult closestResult = null;

        Entity ent;
        for (ProximityResult proximityResult : possibleEnts)
        {
            ent = proximityResult.entity;

            if (ent != origin)
            {
                // Check ent is within correct direction and vertices of ent cross the line i.e. intersection
                collision = castTestOthersideOfLine(lineStart, lineEnd, linePerpStart, linePerpEnd, ent.cachedVertices);

                if (collision)
                {
                    // Find closest intersection between ent edges and lines
                    result = findLineIntersection(origin.positionNew, lineStart, lineEnd, ent.cachedVertices);

                    // Store closest intersection to origin
                    if (result != null && (closestResult == null || result.distance < closestResult.distance))
                    {
                        closestResult = result;
                        closestResult.victim = new EntityCastVictim(ent);
                    }
                }
            }
        }

        return closestResult;
    }

    private static CastingResult castMap(Controller controller, Entity origin, Vector2 lineStart, Vector2 lineEnd,
                                         Vector2 linePerpStart, Vector2 linePerpEnd, float maxDistance)
    {
        // Get the area the line can cross for the map tiles
        // -- Form start and end x/y based on small to large
        float startX;
        float startY;
        float endX;
        float endY;

        // -- X
        if (lineStart.x < lineEnd.x)
        {
            startX = lineStart.x;
            endX = lineEnd.x;
        }
        else
        {
            startX = lineEnd.x;
            endX = lineStart.x;
        }


        // -- Y
        if (lineStart.y < lineEnd.y)
        {
            startY = lineStart.y;
            endY = lineEnd.y;
        }
        else
        {
            startY = lineEnd.y;
            endY = lineStart.y;
        }

        // Now find the start/end indexes for tiles to consider
        WorldMap map = controller.mapService.mainMap;

        int tileStartX = (int) Math.floor(startX / map.tileData.tileSize);
        int tileStartY = (int) Math.floor(startY / map.tileData.tileSize);
        int tileEndX = (int) Math.ceil(endX / map.tileData.tileSize);
        int tileEndY = (int) Math.ceil(endY / map.tileData.tileSize);

        // Clamp to size of map
        tileStartX = CustomMath.limit(0, map.tileData.widthTiles - 1, tileStartX);
        tileStartY = CustomMath.limit(0, map.tileData.heightTiles - 1, tileStartY);
        tileEndX = CustomMath.limit(0, map.tileData.widthTiles - 1, tileEndX);
        tileEndY = CustomMath.limit(0, map.tileData.heightTiles - 1, tileEndY);

        // Iterate each tile
        boolean collision;
        short tileTypeId;
        TileType tileType;
        CastingResult result;
        Vertices tileVertices;

        CastingResult closestResult = null;

        for (int y = tileStartY; y <= tileEndY; y++)
        {
            for (int x = tileStartX; x <= tileEndX; x++)
            {
                tileTypeId = map.tileData.tiles[y][x];
                tileType = map.tileData.tileTypes[tileTypeId];

                // Only perform checks on solid tiles
                if (tileType.properties.solid)
                {
                    tileVertices = map.tileData.tileVertices[y][x];

                    // Check tile is within correct direction and vertices of ent cross the line i.e. intersection
                    collision = castTestOthersideOfLine(lineStart, lineEnd, linePerpStart, linePerpEnd, tileVertices);

                    if (collision)
                    {
                        // Find closest intersection between ent edges and lines
                        result = findLineIntersection(origin.positionNew, lineStart, lineEnd, tileVertices);

                        // Store closest intersection to origin
                        if (result != null && (closestResult == null || result.distance < closestResult.distance))
                        {
                            closestResult = result;
                            closestResult.victim = new MapCastVictim(x, y);
                        }
                    }
                }
            }
        }

        return closestResult;
    }

    private static CastingResult findLineIntersection(Vector2 origin, Vector2 lineStart, Vector2 lineEnd, Vertices vertices)
    {
        // Test each axis for an intersection and pick the closest
        Vector2[] verts = vertices.vertices;

        Vector2 entLineStart;
        Vector2 entLineEnd;
        Vector2 intersection;
        float intersectionDistance;

        Vector2 closestIntersection = null;
        float closestIntersectionDistance = 0.0f;

        for (int i = 0; i < verts.length; i++)
        {
            // Create "line" using edge of ent
            entLineStart = verts[i];
            entLineEnd = verts[i + 1 >= verts.length ? 0 : i + 1];

            // Find the intersection between edge of ent and casted ray/line
            intersection = findLinesIntersection(lineStart, lineEnd, entLineStart, entLineEnd);

            if (intersection != null)
            {
                intersectionDistance = Vector2.distance(intersection, origin);

                // Save closest intersection
                if (closestIntersection == null || intersectionDistance < closestIntersectionDistance)
                {
                    closestIntersection = intersection;
                    closestIntersectionDistance = intersectionDistance;
                }
            }
        }

        if (closestIntersection != null)
        {
            return new CastingResult(closestIntersection.x, closestIntersection.y, closestIntersectionDistance);
        }
        else
        {
            return null;
        }
    }

    private static Vector2 findLinesIntersection(Vector2 aStart, Vector2 aEnd, Vector2 bStart, Vector2 bEnd)
    {
        // http://jsfiddle.net/justin_c_rounds/Gd2S2/
        // http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        float denominator = ((bEnd.y - bStart.y) * (aEnd.x - aStart.x)) - ((bEnd.x - bStart.x) * (aEnd.y - aStart.y));

        float a = aStart.y - bStart.y;
        float b = aStart.x - bStart.x;

        float numerator1 = ((bEnd.x - bStart.x) * a) - ((bEnd.y - bStart.y) * b);
        float numerator2 = ((aEnd.x - aStart.x) * a) - ((aEnd.y - aStart.y) * b);

        a = numerator1 / denominator;
        b = numerator2 / denominator;

        float intersectX = aStart.x + (a * (aEnd.x - aStart.x));
        float intersectY = aStart.y + (a * (aEnd.y - aStart.y));

        boolean aIntersects = a > 0.0f && a < 1.0f;
        boolean bIntersects = b > 0.0f && b < 1.0f;

        return aIntersects && bIntersects ? new Vector2(intersectX, intersectY) : null;
    }

    private static boolean castTestOthersideOfLine(Vector2 lineStart, Vector2 lineEnd, Vector2 linePerpStart, Vector2 linePerpEnd, Vertices vertices)
    {
        // Test each vertex to see if it's left of the line i.e. not within the direction of a bullet
        boolean resultCorrectSide = false;
        boolean resultVerticesCrossLine = false;

        Vector2 vertex;
        boolean firstVertexLeftOfLine = false;
        boolean leftSide;

        for (int i = 0; i < vertices.vertices.length && !(resultCorrectSide && resultVerticesCrossLine); i++)
        {
            vertex = vertices.vertices[i];

            // Test if vertex is on the correct perp side
            leftSide = Vector2.leftSide(linePerpStart, linePerpEnd, vertex);

            if (leftSide)
            {
                resultCorrectSide = true;
            }

            // Test if vertex has crossed the line compared to a previous vertex
            if (i == 0)
            {
                firstVertexLeftOfLine = Vector2.leftSide(lineStart, lineEnd, vertex);
            }
            else
            {
                leftSide = Vector2.leftSide(lineStart, lineEnd, vertex);

                if (leftSide != firstVertexLeftOfLine)
                {
                    resultVerticesCrossLine = true;
                }
            }
        }

        return resultCorrectSide && resultVerticesCrossLine;
    }
}
