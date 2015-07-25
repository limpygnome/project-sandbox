package com.limpygnome.projectsandbox.server.world;

import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.util.Annotations;
import com.limpygnome.projectsandbox.server.util.counters.AnnotationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by limpygnome on 07/07/15.
 */
public class EntTypeMappings
{
    private final static Logger LOG = LogManager.getLogger(EntTypeMappings.class);

    private static final String[] ENTS_CLASS_PATHS = new String[]
    {
            "/com/limpygnome/projectsandbox/server/entity"
    };

    private HashMap<Short, Class> typeIdToClass;
    private HashMap<String, Class> typeNameToClass;

    public EntTypeMappings()
    {
        this.typeIdToClass = new HashMap<>();
        this.typeNameToClass = new HashMap<>();

        LOG.debug("Loading entity types...");

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

        LOG.debug("Loaded {} imp of entities", typeIdToClass.size());
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
