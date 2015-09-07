package com.limpygnome.projectsandbox.server.entity.ai.pathfinding.idle;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.Node;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.Path;

import java.util.*;

/**
 * Created by limpygnome on 07/09/15.
 */
public class DefaultIdleWalkPathBuilder implements IdleWalkPathBuilder
{

    @Override
    public Path build(Entity entity, int maxDepth)
    {
        int tileStartX;
        int tileStartY;

        // Build path to first pedestrian node; this will be our starting place
        List<Node> pathNodes = new LinkedList<>();

        Node lastNode;

        // TODO: build path to first node...add to path nodes collection; set initial capacity to required nodes + max depth

        pathNodes.add(lastNode);

        // Build next nodes to walk...
        HashSet<Node> pathNodesPresent = new HashSet<>();
        int depth = 0;

        while (lastNode != null && ++depth <= maxDepth)
        {
            lastNode = buildNextStep(random, pathNodes, pathNodesPresent, lastNode);
        }

        // Build into path object
        Node[] finalPath = pathNodes.toArray(new Node[pathNodes.size()]);

        Path path = new Path();
        path.finalPath = finalPath;

        return path;
    }

    private Node buildNextStep(Random random, List<Node> pathNodes, Set<Node> pathNodesPresent, Node lastNode)
    {
        // TODO: favour moving in a certain direction...else random...

        // TODO: return null if not found...
        // Fetch all available neighbouring pedestrian nodes
        List<Node> neighboursNew = new LinkedList<>();
        List<Node> neighboursUsed = new LinkedList<>();

        Node node;
        int tileX, tileY;

        for (int y = -1; y <= 1; y++)
        {
            for (int x = -1; x <= 1; x++)
            {
                // Skip center and diagonals
                if (!(y == 0 || x == 0) || (y == 0 && x == 0))
                {
                    continue;
                }

                // Build position
                tileY = lastNode.tileY + y;
                tileX = lastNode.tileX + x;

                // Check if node is within map and pedestrian
                // TODO: the above

                // Build node
                node = new Node(tileY, tileX);

                // Check if present
                if (pathNodesPresent.contains(node))
                {
                    neighboursUsed.add(node);
                }
                else
                {
                    neighboursNew.add(node);
                }
            }
        }

        // Pick either a random new node, else an old node, randomly
        if (!neighboursNew.isEmpty())
        {
            node = neighboursNew.get(random.nextInt(neighboursNew.size()));
        }
        else if (!neighboursUsed.isEmpty())
        {
            node = neighboursUsed.get(random.nextInt(neighboursUsed.size()));
        }
        else
        {
            node = null;
        }

        return node;
    }

}
