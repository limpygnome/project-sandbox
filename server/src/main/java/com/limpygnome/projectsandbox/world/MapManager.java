package com.limpygnome.projectsandbox.world;

import com.limpygnome.projectsandbox.Constants;
import com.limpygnome.projectsandbox.Controller;
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
    
    public HashMap<String, Map> maps;
    private short mapIdCounter;
    
    public Map main;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.maps = new HashMap<>();
        this.mapIdCounter = 0;
        this.main = null;
    }
    
    public void load() throws IOException
    {
        FileSystemFile[] files = FileSystem.getResources(Constants.BASE_PACKAGE_MAPS);

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
