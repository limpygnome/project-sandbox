package com.limpygnome.projectsandbox.server.ents.physics;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
public class Casting
{
    private final static Logger LOG = LogManager.getLogger(Casting.class);

    public static CastingResult cast(Controller controller, Entity origin, float radians, float maxDistance)
    {
        final float RADIANS_90_DEGREES = 1.57079633f;

        // Create line from origin
        Vector2 lineStart = new Vector2(origin.positionNew.x, origin.positionNew.y);
        Vector2 lineEnd = Vector2.vectorFromAngle(radians, maxDistance);
        lineEnd.offset(lineStart);

        // Create perpendicular line from origin
        Vector2 linePerpStart = lineStart.clone();
        Vector2 linePerpEnd = Vector2.vectorFromAngle(radians + RADIANS_90_DEGREES, maxDistance);
        linePerpEnd.offset(linePerpStart);

        // Iterate and find each entity within the radius of being hit
        LinkedList<Entity> possibleEnts = controller.entityManager.nearbyEnts(origin, maxDistance, true);

        // 1. Ignore all the ents which are on the other side of the perp of origin i.e. complete opposite direction
        // 2. Iterate each vertex; a collision has occurred when a vertex is on the different side of the line to
        //    another vertex
        // 3. Perform SAT on the line and each ent; the closest intersection point wins
        boolean collision;

        CastingResult result;
        CastingResult closestResult = null;

        for (Entity ent : possibleEnts)
        {
            if (ent != origin)
            {
                // Check ent is within correct direction and vertices of ent cross the line i.e. intersection
                collision = castTestOthersideOfLine(lineStart, lineEnd, linePerpStart, linePerpEnd, ent);

                if (collision)
                {
                    // Find closest intersection between ent edges and lines
                    result = findLineIntersection(origin.positionNew, lineStart, lineEnd, ent.cachedVertices);

                    // Store closest intersection to origin
                    if (closestResult == null || result.distance < closestResult.distance)
                    {
                        closestResult = result;
                        closestResult.victim = ent;
                    }
                }
            }
        }

        //  Check if we found anything
        if (closestResult == null)
        {
            closestResult = new CastingResult();
        }
        else
        {
            closestResult.collision = true;
        }

        return closestResult;
    }

    private static CastingResult findLineIntersection(Vector2 origin, Vector2 lineStart, Vector2 lineEnd, Vertices vertices)
    {
        // Test each axis for an intersection and pick the closest
        Vector2[] entVerts = vertices.vertices;

        Vector2 entLineStart;
        Vector2 entLineEnd;
        Vector2 intersection;
        float intersectionDistance;

        Vector2 closestIntersection = null;
        float closestIntersectionDistance = 0.0f;

        for (int i = 0; i < entVerts.length; i++)
        {
            // Create "line" using edge of ent
            entLineStart = entVerts[i];
            entLineEnd = entVerts[i + 1 >= entVerts.length ? 0 : i + 1];

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

        return new CastingResult(closestIntersection.x, closestIntersection.y, closestIntersectionDistance);
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

    private static boolean castTestOthersideOfLine(Vector2 lineStart, Vector2 lineEnd, Vector2 linePerpStart, Vector2 linePerpEnd, Entity ent)
    {
        // Test each vertex to see if it's left of the line i.e. not within the direction of a bullet
        boolean resultCorrectSide = false;
        boolean resultVerticesCrossLine = false;

        Vector2 vertex;
        boolean firstVertexLeftOfLine = false;
        boolean leftSide;

        for (int i = 0; i < ent.cachedVertices.vertices.length && !(resultCorrectSide && resultVerticesCrossLine); i++)
        {
            vertex = ent.cachedVertices.vertices[i];

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
