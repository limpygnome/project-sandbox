package com.limpygnome.projectsandbox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author limpygnome
 */
public class JsonHelper
{
    public static JSONObject read(InputStream is) throws IOException
    {
        JSONParser parser = new JSONParser();
        try
        {
            return (JSONObject) parser.parse(new InputStreamReader(is));
        }
        catch(ParseException ex)
        {
            throw new IOException("Failed to parse JSON", ex);
        }
    }
}
