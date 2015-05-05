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
        
        // Create vector of line
        Vector2 lineStart = new Vector2(origin.positionNew.x, origin.positionNew.y);
        Vector2 lineEnd = Vector2.vectorFromAngle(radians, maxDistance);
        lineEnd.offset(lineStart);

        Vector2 linePerpStart = lineStart.clone();
        Vector2 linePerpEnd = Vector2.vectorFromAngle(radians + RADIANS_90_DEGREES, maxDistance);
        linePerpEnd.offset(linePerpStart    );

        LOG.debug("line - {} to {}", lineStart, lineEnd);

        LOG.debug("line perp - {} to {}", linePerpStart, linePerpEnd);

        // Iterate and find each entity within the radius of being hit
        LinkedList<Entity> possibleEnts = controller.entityManager.nearbyEnts(origin, maxDistance, true);

        // 1. Ignore all the ents which are on the other side of the perp of origin i.e. complete opposite direction
        // 2. Iterate each vertex; a collision has occurred when a vertex is on the different side of the line to
        //    another vertex
        // 3. Perform SAT on the line and each ent; the closest intersection point wins
        LinkedList<CollisionResult> collisions = new LinkedList<>();
        boolean collision;

        for (Entity ent : possibleEnts)
        {
            if (ent != origin)
            {
                // Check ent is within correct direction and vertices of ent cross the line
                collision = castTestOthersideOfLine(linePerpStart, linePerpEnd, ent);

                if (collision)
                {
                    LOG.debug(ent.id + " - COLLIDESSSSSSSSSSSSSSS");
                }
                else
                {
                    LOG.debug(ent.id + " - no collision");
                }
            }
        }

        return result;
    }

    private static boolean castTestOthersideOfLine(Vector2 lineStart, Vector2 lineEnd, Entity ent)
    {
        // Test each vertex to see if it's left of the line i.e. not within the direction of a bullet
        boolean resultCorrectSide = false;
        for (Vector2 vertex : ent.cachedVertices.vertices)
        {
            resultCorrectSide = (lineEnd.x - lineStart.x) * (vertex.y - lineStart.y) > (lineEnd.y - lineStart.y) * (vertex.x - lineStart.x);

            if (resultCorrectSide)
            {
                resultCorrectSide = true;
            }
        }

        return resultCorrectSide;
    }
}
