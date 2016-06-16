package com.projectsandbox.components.game.component;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.player.PlayerInfo;

import java.io.Serializable;

/**
 * Allows for ownership to be added to an entity.
 *
 * Useful for weapons avoiding a player's own created entities.
 *
 * WARNING: this component's data is partially transient and thus cannot be relied-upon when it comes to persistence.
 * Therefore this should be null-checked and not critical.
 */
public class OwnershipComponent implements Serializable, EntityComponent
{
    private static final long serialVersionUID = 1L;

    private transient PlayerInfo owner;

    public OwnershipComponent(PlayerInfo owner)
    {
        this.owner = owner;
    }

    public PlayerInfo getOwner()
    {
        return owner;
    }

    public boolean isOwnedBySamePlayer(Entity entity)
    {
        boolean isSame = false;

        if (owner != null && entity != null)
        {
            // Check player first
            PlayerInfo entityPlayer = entity.getPlayer();

            if (entityPlayer != null && owner.equals(entityPlayer))
            {
                isSame = true;
            }
            else
            {
                // Check if ownership attached to entity...
                OwnershipComponent component = (OwnershipComponent) entity.components.fetchComponent(OwnershipComponent.class);

                if (component != null)
                {
                    isSame = owner.equals(component.owner);
                }
            }
        }

        return isSame;
    }

}
