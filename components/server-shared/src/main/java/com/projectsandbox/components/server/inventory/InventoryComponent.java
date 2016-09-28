package com.projectsandbox.components.server.inventory;

import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.PlayerInfoKeyDownComponentEvent;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

/**
 * Created by limpygnome on 28/09/16.
 */
public class InventoryComponent implements EntityComponent, PlayerInfoKeyDownComponentEvent
{
    private Inventory[] inventories;

    @Override
    public void playerInfoKeyDown(PlayerInfo playerInfo, PlayerKeys key)
    {
    }

    @Override
    public void playerInfoKeyUp(PlayerInfo playerInfo, PlayerKeys key)
    {
    }

}
