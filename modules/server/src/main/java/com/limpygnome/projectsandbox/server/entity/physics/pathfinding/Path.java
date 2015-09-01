package com.limpygnome.projectsandbox.server.entity.physics.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by limpygnome on 01/09/15.
 */
public class Path
{
    public HashSet<Node> closedNodes;
    public TreeSet<Node> openNodes;
    public HashMap<TilePosition, Node> nodes;

    public Node[] finalPath;

    public Path()
    {
        this.closedNodes = new HashSet<>();
        this.openNodes = new TreeSet<>();
        this.nodes = new HashMap<>();
    }

    public Node getNode(int index)
    {
        return finalPath[index];
    }

    public int getTotalNodes()
    {
        return finalPath.length;
    }

    public void finalizePath(int targetX, int targetY)
    {
        // Build final path
        Node nodeTarget = nodes.get(new TilePosition(targetX, targetY));

        if (nodeTarget != null && nodeTarget.parent != null)
        {
            // Exclude start node
            finalPath = new Node[nodeTarget.searchDepth];
            int i = nodeTarget.searchDepth;

            // TODO: consider more checking in this region
            while (--i >= 0 && nodeTarget != null)
            {
                finalPath[i] = nodeTarget;
                nodeTarget = nodeTarget.parent;
            }
        }
        else
        {
            finalPath = new Node[0];
        }

        // Destroy data structures used during search
        closedNodes.clear();
        closedNodes = null;
        openNodes.clear();
        openNodes = null;
    }


}
