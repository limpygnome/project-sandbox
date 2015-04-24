package com.limpygnome.projectsandbox.inventory;

import com.limpygnome.projectsandbox.inventory.annotations.InventyoryItemTypeId;
import com.limpygnome.projectsandbox.utils.Annotations;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author limpygnome
 */
public class InventoryManager
{
    public HashMap<Short, Class> types;
    
    public void buildInventoryTypes() throws Exception
    {
        // Load types
        this.types = Annotations.findAnnotatedClasses(InventyoryItemTypeId.class, "/com/limpygnome/projectsandbox/inventory");
        
        // Set the type ID within classes
        Class clazz;
        Field field;
        boolean found;
        
        for (Map.Entry<Short, Class> kv : types.entrySet())
        {
            
            clazz = kv.getValue();
            found = false;

            do
            {
                try
                {
                    field = clazz.getDeclaredField("typeId");
                    field.setAccessible(true);
                    field.setShort(null, kv.getKey());
            
                    found = true;
                }
                catch (Exception e)
                {
                    clazz = clazz.getSuperclass();
                }
            }
            while (!found && clazz != null);

            if (!found)
            {
                throw new IllegalArgumentException("Unable to set typeId on " + kv.getValue().getName());
            }
        }
    }
    
    public void load() throws Exception
    {
        buildInventoryTypes();
    }
}
