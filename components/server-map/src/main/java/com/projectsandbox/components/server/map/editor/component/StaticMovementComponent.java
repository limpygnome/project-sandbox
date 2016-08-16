package com.projectsandbox.components.server.map.editor.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

import java.io.Serializable;

/**
 * Created by limpygnome on 16/08/16.
 */
public class StaticMovementComponent implements Serializable, EntityComponent, LogicComponentEvent
{
    private static final float OFFSET_INCREMENT_PER_CYCLE = 0.1f;

    /* Incremented each logic cycle, in which there's movement, in order to navigate faster. */
    private float accelerate;

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        PlayerInfo playerInfo = playerEntity.getPlayer();

        if (playerInfo != null)
        {
            float offsetX = 0.0f;
            float offsetY = 0.0f;

            // Handle any keys down
            if (playerInfo.isKeyDown(PlayerKeys.MovementUp))
            {
                offsetY += OFFSET_INCREMENT_PER_CYCLE;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementDown))
            {
                offsetY -= OFFSET_INCREMENT_PER_CYCLE;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementLeft))
            {
                offsetX -= OFFSET_INCREMENT_PER_CYCLE;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementDown.MovementRight))
            {
                offsetX += OFFSET_INCREMENT_PER_CYCLE;
            }

            if (offsetX != 0.0f && offsetY != 0.0f)
            {
                // Apply offset
                entity.positionOffset(offsetX, offsetY);

                // Increment acceleration for next cycle if any keys down
                accelerate += OFFSET_INCREMENT_PER_CYCLE;
            }
            else if (accelerate > OFFSET_INCREMENT_PER_CYCLE)
            {
                accelerate = OFFSET_INCREMENT_PER_CYCLE;
            }
        }
    }

}
