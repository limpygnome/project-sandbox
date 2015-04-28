package com.limpygnome.projectsandbox.website.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author limpygnome
 */
public class FileSystemFile
{
    private boolean isFile;
    private String path;
    private String relativePath;

    FileSystemFile(boolean isFile, String path, String relativePath)
    {
        this.isFile = isFile;
        this.path = path;
        this.relativePath = relativePath;
    }
    
    public InputStream getInputStream() throws IOException
    {
        if(isFile)
        {
            return new FileInputStream(path);
        }
        else
        {
            return this.getClass().getResourceAsStream(path);
        }
    }
    
    public boolean isFile()
    {
        return isFile;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public String getRelativePath()
    {
        return relativePath;
    }
}
