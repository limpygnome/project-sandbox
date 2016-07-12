package com.projectsandbox.components.server.util;

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
@Deprecated
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
            // TODO: doesnt start or end with / - bug
            if(!packagePath.startsWith("/"))
            {
                packagePath = "/" + packagePath;
            }
            else if(!packagePath.endsWith("/"))
            {
                packagePath = packagePath + "/";
            }
        
            // Start with top dir
            getResourcesDir(results, path + packagePath, packagePath.substring(0, packagePath.length()-1));
        }
        
        // Return the results
        return results.toArray(new FileSystemFile[results.size()]);
    }
    
    private static void getResourcesDir(LinkedList<FileSystemFile> results, String path, String relativePath) throws IOException
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
                    results.add(new FileSystemFile(true, f.getCanonicalPath(), relativePath + "/" + f.getName()));
                }
                else if(f.isDirectory())
                {
                    getResourcesDir(results, f.getCanonicalPath(), relativePath + "/" + f.getName());
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
                results.add(new FileSystemFile(false, "/" + je.getName(), je.getName()));
            }
        }
    }
    
    public static Class[] getAllClasses(String packageName) throws IOException
    {
        // Locate all class files
        FileSystemFile[] files = getResources(packageName);
        
        LinkedList<Class> result = new LinkedList<>();
        String className;
        Class clazz;
        
        for (FileSystemFile f : files)
        {
            if (f.getPath().endsWith(".class"))
            {
                // Format as class name
                className = f.getRelativePath().replace("/", ".");
                
                // -- Remove starting .
                if (className.startsWith(".") && className.length() > 1)
                {
                    className = className.substring(1);
                }
                
                // -- Remove tailing .class
                if (className.endsWith(".class") && className.length() > 6)
                {
                    className = className.substring(0, className.length()-6);
                }
                
                try
                {
                    // Attempt to load class
                    clazz = Class.forName(className);
                    
                    // Add to results
                    result.add(clazz);
                }
                catch (ClassNotFoundException e) { }
            }
        }
        
        return result.toArray(new Class[result.size()]);
    }
    
}
