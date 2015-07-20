package com.limpygnome.projectsandbox.website.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by limpygnome on 26/04/2015.
 */
public class Settings
{
    private static Settings instance = null;

    private Properties properties;

    public Settings()
    {
        try
        {
            this.properties = new Properties();
            this.properties.load(Settings.class.getResourceAsStream("/settings.properties"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load settings from classpath (settings.properties)", e);
        }
    }

    public String get(String key)
    {
        String v = properties.getProperty(key);

        if (v == null)
        {
            throw new RuntimeException("Key '" + key + "' not present in settings.properties");
        }

        return v;
    }

    public static synchronized Settings getInstance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }

        return instance;
    }
}
