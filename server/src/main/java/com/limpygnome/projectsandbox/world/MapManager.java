package com.limpygnome.projectsandbox.world;

import com.limpygnome.projectsandbox.Constants;
import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.utils.Annotations;
import com.limpygnome.projectsandbox.utils.FileSystem;
import com.limpygnome.projectsandbox.utils.FileSystemFile;
import com.limpygnome.projectsandbox.utils.JsonHelper;
import java.io.IOException;
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
    
    public void buildEntMappings() throws Exception
    {
        this.entTypeMappings = Annotations.findAnnotatedClasses(EntityType.class, "/com/limpygnome/projectsandbox/ents");
    }
    
    public void load() throws Exception
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
