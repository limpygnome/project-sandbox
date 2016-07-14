package com.projectsandbox.components.server.inventory;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.service.EventServerPreStartup;
import com.projectsandbox.components.server.util.Annotations;
import com.projectsandbox.components.server.util.ClassHelper;
import com.projectsandbox.components.server.util.counters.AnnotationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author limpygnome
 */
@Service
public class InventoryManager implements EventServerPreStartup
{
    private final static Logger LOG = LogManager.getLogger(Annotations.class);

    @Autowired
    private ClassHelper classHelper;

    public HashMap<Short, Class> types;

    public InventoryManager()
    {
        this.types = new HashMap<>();
    }

    public void buildInventoryTypes()
    {
        try
        {
            LOG.debug("Building inventory types...");

            // Clear existing imp
            types.clear();

            // Fetch annotated classes
            Set<Class<?>> annotatedClasses = classHelper.fetchClassesByAnnotation(InventoryItemTypeId.class);

            // Build imp store
            InventoryItemTypeId inventoryItemTypeId;
            short typeId;

            for (Class<?> annotatedClass : annotatedClasses)
            {
                inventoryItemTypeId = annotatedClass.getAnnotation(InventoryItemTypeId.class);

                // Fetch required data
                typeId = inventoryItemTypeId.typeId();

                // Check the item doesn't already exist
                if (types.containsKey(typeId))
                {
                    throw new RuntimeException("Type ID already exists for inventory item - type id: " + typeId);
                }

                // Add mapping
                types.put(typeId, annotatedClass);
            }

            // Check against empty map - unlikely / should never happen
            if (this.types.isEmpty())
            {
                throw new RuntimeException("No inventory item types found for inventory manager mapping, looks like an error!");
            }

            LOG.debug("Loaded {} inventory types", this.types.size());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load inventory types", e);
        }
    }

    @Override
    public void eventServerPreStartup(Controller controller)
    {
        buildInventoryTypes();
    }

}
