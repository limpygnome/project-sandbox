package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.inventory.annotation.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.util.Annotations;
import com.limpygnome.projectsandbox.server.util.counters.AnnotationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author limpygnome
 */
@Service
public class InventoryManager
{
    private final static Logger LOG = LogManager.getLogger(Annotations.class);

    // TODO: move string constant to properties file
    private static final String[] CLASSPATHS_INVENTORY_ITEMS =
    {
        "/com/limpygnome/projectsandbox/server/inventory/"
    };

    public HashMap<Short, Class> types;

    public InventoryManager()
    {
        this.types = new HashMap<>();
    }

    public void buildInventoryTypes() throws Exception
    {
        LOG.debug("Building inventory types...");

        // Clear existing imp
        types.clear();

        // Fetch annotated classes
        List<AnnotationInfo> annotationInfoList = Annotations.findAnnotatedClasses(
                InventoryItemTypeId.class,
                CLASSPATHS_INVENTORY_ITEMS,
                true
        );

        // Build imp store
        short typeId;

        for (AnnotationInfo annotationInfo : annotationInfoList)
        {
            // Fetch required data
            typeId = (Short) annotationInfo.getAnnotationValue("typeId");

            // Check the item doesn't already exist
            if (types.containsKey(typeId))
            {
                throw new RuntimeException("Type ID already exists for inventory item - type id: " + typeId);
            }

            // Add mapping
            types.put(typeId, annotationInfo.getClazz());
        }

        // Check against empty map - unlikely / should never happen
        if (this.types.isEmpty())
        {
            throw new RuntimeException("No inventory item types found for inventory manager mapping, looks like an error!");
        }

        LOG.debug("Loaded {} inventory types", this.types.size());
    }
    
    public void load() throws Exception
    {
        buildInventoryTypes();
    }

}
