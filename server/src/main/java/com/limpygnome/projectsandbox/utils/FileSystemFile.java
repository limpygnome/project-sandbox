package com.limpygnome.projectsandbox.utils;

import java.io.File;
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

    FileSystemFile(boolean isFile, String path)
    {
        this.isFile = isFile;
        this.path = path;
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
}
