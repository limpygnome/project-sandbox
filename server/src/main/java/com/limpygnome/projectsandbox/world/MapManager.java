package com.limpygnome.projectsandbox.world;

import com.limpygnome.projectsandbox.Constants;
import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.utils.FileSystem;
import com.limpygnome.projectsandbox.utils.FileSystemFile;
import com.limpygnome.projectsandbox.utils.JsonHelper;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class MapManager
{
    public Controller controller;
    
    public HashMap<Short, Class> entTypeMappings;
    
    public HashMap<String, Map> maps;
    private short mapIdCounter;
    
    public Map main;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.maps = new HashMap<>();
        this.mapIdCounter = 0;
        this.main = null;
        this.entTypeMappings = new HashMap<>();
    }
    
    public void buildEntMappings() throws IOException
    {
        entTypeMappings.clear();
        
        // Iterate all classes to find Entity classes and create map to type ID
        Class[] classes = FileSystem.getAllClasses("/com/limpygnome/projectsandbox/ents");
        
        Annotation annotationEntityType;
        EntityType entType;
        for (Class clazz : classes)
        {
            if (clazz.isAnnotationPresent(EntityType.class))
            {
                // Read annotation
                annotationEntityType = clazz.getAnnotation(EntityType.class);
                entType = (EntityType) annotationEntityType;
                
                if (entType.typeId() != 0)
                {
                    // Create mapping
                    entTypeMappings.put(entType.typeId(), clazz);
                    System.out.println("Added ent mapping - " + entType.typeId() + " -> " + clazz.getName());
                }
                else if (clazz != Entity.class)
                {
                    System.err.println("Entity class '" + clazz.getName() + "' has invalid EntityType annotation.");
                }
            }
        }
    }
    
    public void load() throws IOException
    {
        FileSystemFile[] files = FileSystem.getResources(Constants.BASE_PACKAGE_MAPS);
        
        // Build ent mappings
        buildEntMappings();

        // Iterate and load each map file
        JSONObject obj;
        Map map;
        for(FileSystemFile file : files)
        {
            // Load map
            obj = JsonHelper.read(file.getInputStream());
            map = Map.load(this, obj);
            
            // Assign ID
            map.id = mapIdCounter++;
            
            // Add mapping
            maps.put(map.name, map);
            
            System.out.println("Map manager - loaded map  - " + map.toString());
        }
        
        // Set the main map file
        main = maps.get("main");
        
        if(main == null)
        {
            throw new IOException("Unable to find main map.");
        }
    }
    
}
