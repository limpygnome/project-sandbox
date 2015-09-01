package com.limpygnome.projectsandbox.server.entity.physics.pathfinding.astar;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Node;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.PathFinder;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.TilePosition;
import com.limpygnome.projectsandbox.server.world.Map;
import com.limpygnome.projectsandbox.server.world.TileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Path finder implementation using A* algorithm.
 *
 * Thread-safe.
 */
public class AStarPathFinder implements PathFinder
{
    private final static Logger LOG = LogManager.getLogger(AStarPathFinder.class);

    public static final int MAX_DEPTH = 200;

    private AStarHeuristic heuristic;

    public AStarPathFinder(AStarHeuristic heuristic)
    {
        this.heuristic = heuristic;
    }

    @Override
    public Path findPath(Map map, Entity entity, float startX, float startY, float endX, float endY)
    {
        // Convert positions into tiles
        int tileSize = (int) map.tileSize;

        int startTileX = (int) (startX / tileSize);
        int startTileY = (int) (startY / tileSize);
        int endTileX = (int) (endX / tileSize);
        int endTileY = (int) (endY / tileSize);

        // The path instance holds our progress and end result
        Path path = new Path();

        // Check start tile is usable
        if (!isTileUsable(map, entity, startTileX, startTileY))
        {
            return path;
        }

        // Add start node to path as initial starting place
        {
            Node startNode = new Node(startTileX, startTileY, 0.0f, 0);

            path.nodes.put(new TilePosition(startTileX, startTileY), startNode);
            path.openNodes.add(startNode);
        }

        int depth = 0;
        Node currentNode;
        Node neighborNode;
        int offsetX, offsetY;
        boolean found = false;
        int neighborX;
        int neighborY;

        while ((depth <= MAX_DEPTH) && !path.openNodes.isEmpty())
        {
            currentNode = path.openNodes.pollFirst();

            // Check if we reached the target node
            if (currentNode.tileX == endTileX && currentNode.tileY == endTileY)
            {
                found = true;
                break;
            }

            // Add current node to closed list
            path.closedNodes.add(currentNode);

            // Search neighboring nodes
            for (offsetX = -1; offsetX <= 1; offsetX++)
            {
                for (offsetY = -1; offsetY <= 1; offsetY++)
                {
                    // Don't check if not a neighbor i.e. current node - 0,0
                    if ((offsetX == 0) && (offsetY == 0))
                    {
                        continue;
                    }

                    // Compute location on map
                    neighborX = currentNode.tileX + offsetX;
                    neighborY = currentNode.tileY + offsetY;

                    // Check within bounds of map
                    if (neighborX < 0 || neighborY < 0 || neighborX > map.width || neighborY > map.height)
                    {
                        continue;
                    }

                    // Check usable; if so, process
                    if (isTileUsable(map, entity, neighborX, neighborY))
                    {
                        neighborNode = processNeighbor(map, entity, path, currentNode, neighborX, neighborY, endTileX, endTileY);
                        depth = Math.max(depth, neighborNode.searchDepth);
                    }
                }
            }
        }

        // Output dev message
        if (LOG.isDebugEnabled())
        {
            if (found)
            {
                LOG.debug("Path found");
            }
            else if (depth > MAX_DEPTH)
            {
                LOG.debug("Max depth exceeded whilst finding path");
            }
            else if (path.openNodes.isEmpty())
            {
                LOG.debug("No open nodes left whilst finding path");
            }
        }

        // Finalize path of nodes for entity to travel
        path.finalizePath(endTileX, endTileY);

        return path;
    }

    // Returns max depth
    private Node processNeighbor(Map map, Entity entity, Path path, Node currentNode, int neighborX, int neighborY, int targetTileX, int targetTileY)
    {
        float neighborCost = currentNode.pathCost + 1;
        TilePosition tilePosition = new TilePosition(neighborX, neighborY);

        Node neighborNode = path.nodes.get(tilePosition);

        // Add node if it doesn't exist
        boolean processNode;

        if (neighborNode == null)
        {
            // Add new node for processing
            neighborNode = new Node(neighborX, neighborY);
            neighborNode.parent = currentNode;
            neighborNode.searchDepth = neighborNode.parent.searchDepth + 1;

            path.nodes.put(tilePosition, neighborNode);

            processNode = true;
        }
        else
        {
            // Determine if node needs reprocessing
            processNode = (neighborCost < neighborNode.pathCost);

            if (processNode)
            {
                path.openNodes.remove(neighborNode);
                path.closedNodes.remove(neighborNode);
            }
        }

        // Determine if to add node to (re-)process node
        if (processNode)
        {
            neighborNode.pathCost = neighborCost;
            neighborNode.heuristicCost = heuristic.getCost(map, entity, neighborX, neighborY, targetTileX, targetTileY);
        }

        return neighborNode;
    }

    private boolean isTileUsable(Map map, Entity entity, int tileX, int tileY)
    {
        TileType tileType = map.tileTypes[map.tiles[tileY][tileX]];

        // TODO: improve by looking at size of entity and neighbors being solid
        return !tileType.properties.solid;

    }

}
