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

        CastingResult result = new CastingResult();
        
        // Create line from origin
        Vector2 lineStart = new Vector2(origin.positionNew.x, origin.positionNew.y);
        Vector2 lineEnd = Vector2.vectorFromAngle(radians, maxDistance);
        lineEnd.offset(lineStart);

        // Create perpendicular line from origin
        Vector2 linePerpStart = lineStart.clone();
        Vector2 linePerpEnd = Vector2.vectorFromAngle(radians + RADIANS_90_DEGREES, maxDistance);
        linePerpEnd.offset(linePerpStart);

        // Create vertices for bullet line
        Vertices bulletLineVertices = new Vertices(origin.positionNew, new Vector2[]{ lineStart, lineEnd });

        // Iterate and find each entity within the radius of being hit
        LinkedList<Entity> possibleEnts = controller.entityManager.nearbyEnts(origin, maxDistance, true);

        // 1. Ignore all the ents which are on the other side of the perp of origin i.e. complete opposite direction
        // 2. Iterate each vertex; a collision has occurred when a vertex is on the different side of the line to
        //    another vertex
        // 3. Perform SAT on the line and each ent; the closest intersection point wins
        boolean collision;
        CollisionResult collisionResult;
        float distance;

        CollisionResult closestCollisionResult = null;
        float closestDistance = 0.0f;
        Entity closestEnt = null;

        for (Entity ent : possibleEnts)
        {
            if (ent != origin)
            {
                // Check ent is within correct direction and vertices of ent cross the line i.e. intersection
                collision = castTestOthersideOfLine(lineStart, lineEnd, linePerpStart, linePerpEnd, ent);

                if (collision)
                {
                    // Perform SAT to retrieve the MTV
                    collisionResult = SAT.collision(ent.cachedVertices, bulletLineVertices);

                    // Check we did get a collision, or something odd occurred
                    if (!collisionResult.collision)
                    {
                        LOG.warn("Initial steps found bullet collision, but SAT did not - ent id: {}, origin id: {}", ent.id, origin.id);
                    }
                    else
                    {
                        distance = Vector2.distance(lineStart, collisionResult.mtv);

                        // Store closest result from each ent
                        if (closestCollisionResult == null || distance < closestDistance)
                        {
                            closestCollisionResult = collisionResult;
                            closestDistance = distance;
                            closestEnt = ent;
                        }
                    }
                }
            }
        }

        // Check if we found a result
        if (closestCollisionResult != null)
        {
            // Copy to actual result for this call
            result.distance = closestDistance;
            result.collision = true;
            result.collisionResult = closestCollisionResult;
            result.victim = closestEnt;

            result.x = closestEnt.positionNew.x + closestCollisionResult.mtv.x;
            result .y = closestEnt.positionNew.y + closestCollisionResult.mtv.y;
        }

        return result;
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
