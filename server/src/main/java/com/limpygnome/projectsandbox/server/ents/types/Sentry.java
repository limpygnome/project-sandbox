package com.limpygnome.projectsandbox.server.ents.types;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;

/**
 * Created by limpygnome on 12/05/15.
 */
@EntityType(typeId = 500)
public class Sentry extends Entity
{
    public final static float RANGE = 300.0f;
    public final static float ROTATION_RATE = 0.5f;

    public Sentry()
    {
        super((short) 32, (short) 32);
    }

    @Override
    public void logic(Controller controller)
    {
        // Find nearest ent

        // Rotate turret towards ent

        // Fire on ent if sentry aligns with target

        // Perform ent logic
        super.logic(controller);
    }

    @Override
    public void reset()
    {
    }
}
