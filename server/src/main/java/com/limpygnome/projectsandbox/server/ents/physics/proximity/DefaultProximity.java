package com.limpygnome.projectsandbox.server.ents.physics.proximity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: refactor this class to be a quadtree.
 *
 * Created by limpygnome on 13/05/15.
 */
public class DefaultProximity
{
    public static List<ProximityResult> nearbyEnts(Controller controller, Entity a, float distance, boolean testAllVertices, boolean sortList)
    {
        LinkedList<ProximityResult> result = new LinkedList<>();

        synchronized (controller.entityManager.entities)
        {
            Entity b;
            float entDistance;

            for (Map.Entry<Short, Entity> kv : controller.entityManager.entities.entrySet())
            {
                b = kv.getValue();

                if (a != b)
                {
                    // Get distance to center
                    entDistance = Vector2.distance(a.positionNew, b.positionNew);

                    // Check if we're only performing a simple  test
                    if (!testAllVertices)
                    {
                        if (entDistance <= distance)
                        {
                            result.add(new ProximityResult(b, distance));
                        }
                    }
                    else if (entDistance <= distance + b.cachedVertices.collisionRadius)
                    {
                        // Test all the vertices - expensive!
                        // -- First find the closest vertex to the center
                        Vector2 closestVertex = null;
                        float closestDistance = 0.0f;
                        int i;

                        for (i = 0; i < a.cachedVertices.vertices.length; i++)
                        {
                            entDistance = Vector2.distance(a.cachedVertices.vertices[i], b.positionNew);

                            if (closestVertex == null || entDistance < closestDistance)
                            {
                                closestVertex = a.cachedVertices.vertices[i];
                                closestDistance = entDistance;
                            }
                        }

                        // -- Find closest vertex of ent being tested
                        if (closestVertex != null)
                        {
                            Vector2 closestVertexB = null;
                            for (i = 0; i < b.cachedVertices.vertices.length; i++)
                            {
                                entDistance = Vector2.distance(closestVertex, b.cachedVertices.vertices[i]);

                                if (closestVertexB == null || entDistance < closestDistance)
                                {
                                    closestVertexB = b.cachedVertices.vertices[i];
                                    closestDistance = entDistance;
                                }
                            }

                            // Check we found a result
                            if (closestVertexB != null)
                            {
                                result.add(new ProximityResult(b, closestDistance, closestVertex, closestVertexB));
                            }
                        }
                    }
                }
            }
        }

        // Check if to sort list
        if (sortList)
        {
            // TODO: consider performance of call versus sorting on insert versus our own sorting algorithm, this is probably best though
            Collections.sort(result);
        }

        return result;
    }
}
