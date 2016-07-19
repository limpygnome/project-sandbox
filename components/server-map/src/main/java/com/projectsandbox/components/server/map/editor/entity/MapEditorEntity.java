package com.projectsandbox.components.server.map.editor.entity;

import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Created by limpygnome on 07/07/16.
 */
@EntityType(typeId = 123, typeName = "util/map-editor")
public class MapEditorEntity extends PlayerEntity
{

    public MapEditorEntity(WorldMap map)
    {
        super(
                map,
                (short) 16,
                (short) 16,
                new PlayerInfo[1],
                null
        );
    }

    @Override
    public String entityName()
    {
        return "Map Editor";
    }

}
