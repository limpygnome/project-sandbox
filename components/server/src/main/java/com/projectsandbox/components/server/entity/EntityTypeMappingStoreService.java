package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.util.Annotations;
import com.projectsandbox.components.server.util.counters.AnnotationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Finds all of the annotated entities and builds a map for dynamically creating instances by name or identifier.
 */
@Service
public class EntityTypeMappingStoreService
{
    private final static Logger LOG = LogManager.getLogger(EntityTypeMappingStoreService.class);

    // TODO: move into a settings file
    private static final String[] ENTS_CLASS_PATHS = new String[]
    {
            "/com/projectsandbox/components/server/entity",
            "/com/projectsandbox/components/game/entity"
    };

    private HashMap<Short, Class> typeIdToClass;
    private HashMap<String, Class> typeNameToClass;

    public EntityTypeMappingStoreService()
    {
        this.typeIdToClass = new HashMap<>();
        this.typeNameToClass = new HashMap<>();

        LOG.debug("loading entity types...");

        // Fetch annotations
        List<AnnotationInfo> annotationInfoList;

        try
        {
            annotationInfoList = Annotations.findAnnotatedClasses(
                    EntityType.class,
                    ENTS_CLASS_PATHS,
                    true
            );
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to build annotation info for ent types", e);
        }

        // Create mappings
        Class clazz;
        short typeId;
        String typeName;

        for (AnnotationInfo annotationInfo : annotationInfoList)
        {
            // Fetch required data
            clazz = annotationInfo.getClazz();
            typeId = (Short) annotationInfo.getAnnotationValue("typeId");
            typeName = (String) annotationInfo.getAnnotationValue("typeName");

            // Check mappings dont exist
            if (typeIdToClass.containsKey(typeId))
            {
                throw new RuntimeException("Type ID already exists - type id: " + typeId);
            }
            else if (typeNameToClass.containsKey(typeName))
            {
                throw new RuntimeException("Type name already exists - type name: " + typeName);
            }

            // Create new mappings
            typeIdToClass.put(typeId, clazz);
            typeNameToClass.put(typeName, clazz);
        }

        LOG.debug("loaded {} imp of entities", typeIdToClass.size());
    }

    public Class getEntityClassByTypeId(short typeId)
    {
        return typeIdToClass.get(typeId);
    }

    public Class getEntityClassByTypeName(String typeName)
    {
        return typeNameToClass.get(typeName);
    }

}
