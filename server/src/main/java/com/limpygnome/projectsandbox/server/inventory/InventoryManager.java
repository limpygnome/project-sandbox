package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;
import com.limpygnome.projectsandbox.server.utils.Annotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author limpygnome
 */
public class InventoryManager
{
    private final static Logger LOG = LogManager.getLogger(Annotations.class);

    public HashMap<Short, Class> types;
    
    public void buildInventoryTypes() throws Exception
    {
        LOG.debug("Building inventory types...");

        // Load types
        // TODO: move string constant to properties file
        this.types = Annotations.findAnnotatedClasses(
                InventyoryItemTypeId.class,
                "/com/limpygnome/projectsandbox/server/inventory/"
        );

        // Check against empty map - unlikely / should never happen
        if (this.types.isEmpty())
        {
            throw new RuntimeException("No inventory item types found for inventory manager mapping, looks like an error!");
        }
    }
    
    public void load() throws Exception
    {
        buildInventoryTypes();
    }
}
