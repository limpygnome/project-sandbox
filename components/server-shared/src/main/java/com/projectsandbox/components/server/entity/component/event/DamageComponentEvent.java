package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;

/**
 * Used by a component to handle damage.
 */
public interface DamageComponentEvent
{

    /**
     * Invoked when damage occurs to the entity.
     *
     * Since this follows a chain pattern, the returned damage will be passed to the next component, or it will be used
     * and the final returned health applied to the entity.
     *
     * @param controller the controller
     * @param inflicter the inflicter of damage; can be null
     * @param damage the amount of damage; can be negative for healing
     * @return the final amount of damage; can be negative for healing
     */
    float eventDamage(Controller controller, Entity inflicter, float damage);

}
