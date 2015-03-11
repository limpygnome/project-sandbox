package com.limpygnome.projectsandbox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * NOTE: jar will include sub-dirs, dir will not include subdirs or files in subdirs
 * @author limpygnome
 */
public class FileSystem
{
    public static FileSystemFile[] getResources(String packagePath) throws IOException
    {
        LinkedList<FileSystemFile> results = new LinkedList<>();
        
        // Determine if we're inside a jar or classes dir
        String path = FileSystem.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if(path.endsWith(".jar"))
        {
            getResourcesJar(results, path, packagePath);
        }
        else
        {
            // Make sure the path starts with /
            if(!packagePath.startsWith("/"))
            {
                packagePath = "/" + packagePath;
            }
            else if(!packagePath.endsWith("/"))
            {
                packagePath = packagePath + "/";
            }
        
            // Start with top dir
            getResourcesDir(results, path + packagePath);
        }
        
        // Return the results
        return results.toArray(new FileSystemFile[results.size()]);
    }
    
    private static void getResourcesDir(LinkedList<FileSystemFile> results, String path) throws IOException
    {
        // Fetch files
        File dir = new File(path);
        
        if(dir.exists())
        {
            for(File f : dir.listFiles())
            {
                if(f.isFile())
                {
                    // Add file to results
                    results.add(new FileSystemFile(true, f.getCanonicalPath()));
                }
                else if(f.isDirectory())
                {
                    getResourcesDir(results, f.getCanonicalPath());
                }
            }
        }
    }
    
    private static void getResourcesJar(LinkedList<FileSystemFile> results, String path, String packagePath) throws IOException
    {
        // Make sure package path does not start with /, but ends with /
        if(packagePath.startsWith("/") && packagePath.length() > 1)
        {
            packagePath = packagePath.substring(1);
        }
        if(!packagePath.endsWith("/"))
        {
            packagePath = packagePath + "/";
        }
        
        // Open up our jar
        JarInputStream jis = new JarInputStream(new FileInputStream(path));
        JarEntry je;
        
        // Iterate all the entries, filtering by dir
        String name;
        while((je = jis.getNextJarEntry()) != null)
        {
            name = je.getName();
            
            if(!name.endsWith("/") && name.startsWith(packagePath))
            {
                results.add(new FileSystemFile(false, "/" + je.getName()));
            }
        }
    }
}
