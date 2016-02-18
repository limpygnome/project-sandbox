package com.limpygnome.projectsandbox.server.entity.ai.pathfinding.idle;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.Node;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.Path;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.MapPosition;

import com.limpygnome.projectsandbox.server.world.tile.TileType;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by limpygnome on 07/09/15.
 */
public class DefaultIdleWalkPathBuilder implements IdleWalkPathBuilder
{

    @Override
    public Path build(Controller controller, WorldMap map, Entity entity, int maxDepth)
    {
        MapPosition entityPosition = controller.mapManager.mainMap.positionFromReal(entity.positionNew);

        // Build path to first pedestrian node; this will be our starting place
        List<Node> pathNodes = new LinkedList<>();

        Node lastNode = findStartNode(map, pathNodes, entityPosition, maxDepth);
        pathNodes.add(lastNode);

        int nodesToStart = pathNodes.size();

        // Build next nodes to walk...
        Random random = new Random(System.currentTimeMillis());
        HashSet<Node> pathNodesPresent = new HashSet<>();
        int depth = 0;

        while (lastNode != null && ++depth <= maxDepth)
        {
            lastNode = buildNextStep(map, random, pathNodesPresent, pathNodes, lastNode);

            // Add the newly found node...
            if (lastNode != null)
            {
                pathNodes.add(lastNode);
            }
        }

        // Build into path object
        Node[] finalPath = pathNodes.toArray(new Node[pathNodes.size()]);

        // TODO: this feels a little hacky, improve...
        Path path = new Path();
        path.finalPath = finalPath;
        path.nodeSeparation = map.tileSizeHalf;

        // Ensure every node has vector built
        for (int i = 0; i < path.finalPath.length; i++)
        {
            path.finalPath[i].buildAndCacheXY(map);
        }

        // Offset nodes so that two ents walking towards each other have less chance of colliding
        // TODO: improve this calculation for half tilesize, we do it above too...perhaps cache in map...
        offsetPedestrianNodes(path, nodesToStart, map);

        return path;
    }

    private Node findStartNode(WorldMap map, List<Node> pathNodes, MapPosition entityPosition, int maxDepth)
    {
        // NOTE: this is very similar to the A* path-finding code in some ways...

        // Build start node
        Node startNode = new Node(entityPosition.tileX, entityPosition.tileY, 0.0f, 1);

        // Search for closest pedestrian node
        TreeSet<Node> openNodes = new TreeSet<>();
        HashSet<Node> closedNodes = new HashSet<>();
        openNodes.add(startNode);

        Node currentNode;
        Node newNode;
        TileType tileType;
        int depth = 0;

        while (depth <= maxDepth && !openNodes.isEmpty())
        {
            // Pick closest node
            currentNode = openNodes.pollFirst();

            // Update depth
            depth = Math.max(depth, currentNode.searchDepth);

            // Check if current node is suitable as candidate
            tileType = map.tileTypes[map.tiles[currentNode.tileY][currentNode.tileX]];

            if (tileType.properties.pedestrian)
            {
                return currentNode;
            }

            // Add current node to searched nodes, to avoid repeating search later
            closedNodes.add(currentNode);

            // Add neighbours
            for (int y = -1; y <= 1; y++)
            {
                for (int x = -1; x <= 1; x++)
                {
                    newNode = new Node(currentNode.tileX + x, currentNode.tileY + y, currentNode.pathCost + 1, currentNode.searchDepth + 1);

                    if (!closedNodes.contains(newNode))
                    {
                        openNodes.add(newNode);
                    }
                }
            }
        }

        return null;
    }

    private Node buildNextStep(WorldMap map, Random random, Set<Node> pathNodesPresent, List<Node> pathNodes, Node lastNode)
    {
        // Check node before last node for direction; looks more natural than chaotic picking
        if (pathNodes.size() > 2)
        {
            Node lastLastNode = pathNodes.get(pathNodes.size()-2);

            // Compute next tile based on direction travelled between the last two nodes
            int nextTileX = lastNode.tileX + (lastNode.tileX - lastLastNode.tileX);
            int nextTileY = lastNode.tileY + (lastNode.tileY - lastLastNode.tileY);

            TileType tileType = map.tileTypeFromPosition(nextTileX, nextTileY);

            if (tileType != null && tileType.properties.pedestrian)
            {
                return new Node(nextTileX, nextTileY);
            }
        }

        // We'll need to randomly pick a neighbour - fetch all available neighbouring pedestrian nodes
        List<Node> neighboursNew = new LinkedList<>();
        List<Node> neighboursUsed = new LinkedList<>();

        Node node;
        int tileX, tileY;
        TileType tileType;

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
                if (tileX < 0 || tileY < 0 || tileX >= map.width || tileY >= map.height)
                {
                    continue;
                }

                tileType = map.tileTypes[map.tiles[tileY][tileX]];

                if (!tileType.properties.pedestrian)
                {
                    continue;
                }

                // Build node
                node = new Node(tileX, tileY);

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

    private void offsetPedestrianNodes(Path path, int offsetStart, WorldMap map)
    {
        int totalNodes = path.finalPath.length;

        Node previousNode = null;
        Node currentNode;

        float differenceX, differenceY;

        for (int i = offsetStart; i < totalNodes; i++)
        {
            currentNode = path.finalPath[i];

            if (previousNode != null)
            {
                // Compute the multipliers for the offset
                differenceX = offsetPedestrianNodes_convertToOne(
                        currentNode.cachedVector.x - previousNode.cachedVector.x
                );
                differenceY = offsetPedestrianNodes_convertToOne(
                        currentNode.cachedVector.y - previousNode.cachedVector.y
                );

                // Multiply by offset
                differenceX *= -map.tileSizeQuarter;
                differenceY *= -map.tileSizeQuarter;

                // Apply to node
                currentNode.cachedVector.x += differenceY;
                currentNode.cachedVector.y += differenceX;
            }

            previousNode = currentNode;
        }
    }

    private float offsetPedestrianNodes_convertToOne(float value)
    {
        if (value == 0.0f)
        {
            return 0.0f;
        }
        else if (value < 0.0f)
        {
            return -1.0f;
        }
        else
        {
            return 1.0f;
        }
    }

}
