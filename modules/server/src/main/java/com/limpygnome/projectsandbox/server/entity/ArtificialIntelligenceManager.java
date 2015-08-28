package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.ai.ComputedPath;

/**
 * Created by limpygnome on 15/07/15.
 */
public class ArtificialIntelligenceManager
{
    private Controller controller;

    public ArtificialIntelligenceManager(Controller controller)
    {
        this.controller = controller;
    }

    public ComputedPath computedPathToTarget(Entity entity, Entity target)
    {
        // TODO: complete this...
    }

    public void buildNetwork()
    {
        // Construct tree of walkable routes
        // TODO: consider existing techniques
    }
}
