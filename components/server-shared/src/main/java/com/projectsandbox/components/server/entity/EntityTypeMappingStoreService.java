package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.util.Annotations;
import com.projectsandbox.components.server.util.ClassHelper;
import com.projectsandbox.components.server.util.counters.AnnotationInfo;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Finds all of the annotated entities and builds a map for dynamically creating instances by name or identifier.
 */
@Service
public class EntityTypeMappingStoreService
{
    private final static Logger LOG = LogManager.getLogger(EntityTypeMappingStoreService.class);

    @Autowired
    private ClassHelper classHelper;

    private HashMap<Short, Class> typeIdToClass;
    private HashMap<String, Class> typeNameToClass;

    public EntityTypeMappingStoreService()
    {
        this.typeIdToClass = new HashMap<>();
        this.typeNameToClass = new HashMap<>();
    }

    @PostConstruct
    public void setup()
    {
        LOG.debug("loading entity types...");

        // Fetch annotations
        Set<Class<?>> annotatedClasses = classHelper.fetchClassesByAnnotation(EntityType.class);

        // Create mappings
        EntityType entityType;
        short typeId;
        String typeName;

        for (Class<?> annotatedClass : annotatedClasses)
        {
            entityType = annotatedClass.getAnnotation(EntityType.class);

            // Fetch required data
            typeId = entityType.typeId();
            typeName = entityType.typeName();

            // Check mappings dont exist
            if (typeIdToClass.containsKey(typeId))
            {
                throw new RuntimeException("Type ID already exists - type id: " + typeId + " - already mapped: " + typeIdToClass.get(typeId).getName() + ", current class: " + annotatedClass.getName());
            }
            else if (typeNameToClass.containsKey(typeName))
            {
                throw new RuntimeException("Type name already exists - type name: " + typeName);
            }

            // Create new mappings
            typeIdToClass.put(typeId, annotatedClass);
            typeNameToClass.put(typeName, annotatedClass);

            LOG.debug("loaded type - id: {}, name: {}, class: {}", typeId, typeName, annotatedClass);
        }

        LOG.debug("loaded {} imp of entities", typeIdToClass.size());
    }

    public Entity createByTypeId(short typeId, MapEntKV entityData)
    {
        Class<? extends Entity> type = typeIdToClass.get(typeId);

        if (type == null)
        {
            throw new RuntimeException("Entity type ID does not exist - " + typeId);
        }

        return createInstance(type, entityData);
    }

    public Entity createByTypeName(String typeName, MapEntKV entityData)
    {
        Class<? extends Entity> type = typeNameToClass.get(typeName);

        if (type == null)
        {
            throw new RuntimeException("Entity type name does not exist - " + typeName);
        }

        return createInstance(type, entityData);
    }

    private Entity createInstance(Class<? extends Entity> type, MapEntKV entityData)
    {
        try
        {
            Entity entity = type.newInstance();

            if (entityData != null)
            {
                entity.applyMapKeyValues(entityData);
            }

            return entity;
        }
        catch (IllegalAccessException | InstantiationException e)
        {
            throw new RuntimeException("Failed to create entity - type: " + type.getName(), e);
        }
    }

}
