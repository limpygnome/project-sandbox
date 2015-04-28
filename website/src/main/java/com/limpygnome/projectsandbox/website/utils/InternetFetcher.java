package com.limpygnome.projectsandbox.website.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;

/**
 * Created by limpygnome on 26/04/2015.
 */
public class InternetFetcher
{
    private final static Logger LOG = LogManager.getLogger(InternetFetcher.class);
    private static final int CHUNK_SIZE = 4096;

    public static String fetchString(URL url) throws IOException
    {
        byte[] data = fetch(url);
        return new String(data, "UTF-8");
    }

    public static byte[] fetch(URL url) throws IOException
    {
        LOG.info("Fetching resource from " + url);

        // Open stream
        InputStream is = url.openStream();

        // Read byte data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] chunk = new byte[CHUNK_SIZE];

        // Read as string
        while ((read = is.read(chunk)) != -1)
        {
            baos.write(chunk, 0 , read);
        }

        // Dispose stream
        is.close();

        byte[] data = baos.toByteArray();

        LOG.info("Resource from " + url + " is " + data.length + " bytes");

        return data;
    }
}
