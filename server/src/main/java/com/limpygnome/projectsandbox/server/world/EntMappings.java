package com.limpygnome.projectsandbox.server.world;

import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.utils.Annotations;

import java.util.HashMap;

/**
 * Created by limpygnome on 07/07/15.
 */
public class EntMappings
{
    HashMap<Short, Class> typeIdToClass;
    HashMap<String, Class> typeNameToClass;

    public EntMappings() throws Exception
    {
        this.typeIdToClass = new HashMap<>();
        this.typeNameToClass = new HashMap<>();

        this.typeIdToClass = Annotations.findAnnotatedClasses(EntityType.class, "/com/limpygnome/projectsandbox/server/ents")
    }

    public Class getMappingByTypeId(short typeId)
    {
        return typeIdToClass.get(typeId);
    }

    public Class getMappingByTypeName(String typeName)
    {
        return typeNameToClass.get(typeName);
    }
}
