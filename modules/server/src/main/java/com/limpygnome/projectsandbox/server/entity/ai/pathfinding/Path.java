package com.limpygnome.projectsandbox.server.entity.ai.pathfinding;

import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.world.Map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Used to hold the result of nodes to traverse and follow from a path-finding computation.
 */
public class Path
{
    /**
     * The final path to follow.
     *
     * You should not access this directly; public only for performance purposes.
     */
    public Node[] finalPath;

    /**
     * The distance between nodes.
     */
    public float nodeSeparation;

    public Node getNode(int index)
    {
        return finalPath[index];
    }

    public int getTotalNodes()
    {
        return finalPath.length;
    }

    public float getTargetNodeDistance(Vector2 vector)
    {
        if (finalPath.length > 0)
        {
            Node targetNode = finalPath[finalPath.length - 1];
            return Vector2.distance(vector, targetNode.cachedVector);
        }

        return 0.0f;
    }

}
