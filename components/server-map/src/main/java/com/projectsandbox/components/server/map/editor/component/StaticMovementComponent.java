package com.projectsandbox.components.server.map.editor.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

/**
 * Simple component to statically move around the map fast.
 *
 * Intended for map editing entities.
 */
public class StaticMovementComponent implements EntityComponent, LogicComponentEvent
{
    /* The amount of movement added during each cycle when keys are continuously held. */
    private static final float OFFSET_INCREMENT_PER_CYCLE = 0.5f;

    /* The amount moved per cycle when holding shift key. */
    private static final float SUPER_JUMP_AMOUNT = 1000.0f;

    /* Incremented each logic cycle, in which there's movement, in order to navigate faster. */
    private float accelerate;

    public StaticMovementComponent()
    {
        accelerate = OFFSET_INCREMENT_PER_CYCLE;
    }

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
                offsetY += accelerate;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementDown))
            {
                offsetY -= accelerate;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementLeft))
            {
                offsetX -= accelerate;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementDown.MovementRight))
            {
                offsetX += accelerate;
            }

            if (offsetX != 0.0f || offsetY != 0.0f)
            {
                // Check if to apply super-jump instead
                if (playerInfo.isKeyDown(PlayerKeys.Shift))
                {
                    // Need to look at incrementing super-jump amount each cycle...
                    offsetX = (offsetX > 0.0f ? SUPER_JUMP_AMOUNT : offsetX < 0.0f ? -SUPER_JUMP_AMOUNT : 0.0f);
                    offsetY = (offsetY > 0.0f ? SUPER_JUMP_AMOUNT : offsetY < 0.0f ? -SUPER_JUMP_AMOUNT : 0.0f);
                }
                else
                {
                    // Increment acceleration for next cycle for continuous keys
                    accelerate += OFFSET_INCREMENT_PER_CYCLE;
                }

                // Apply offset
                entity.positionOffset(offsetX, offsetY);
            }
            else if (accelerate > OFFSET_INCREMENT_PER_CYCLE)
            {
                accelerate = OFFSET_INCREMENT_PER_CYCLE;
            }
        }
    }

}
