package com.projectsandbox.components.shared.model;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.SerializationException;

import java.io.Serializable;

/**
 * A wrapper for persisting game session data safely, which allows serialization to fail gracefully.
 */
public class GameSessionDataWrapper implements Serializable
{
    private final static Logger LOG = LogManager.getLogger(GameSessionDataWrapper.class);

    private static final long serialVersionUID = 1L;

    // The actual serialized data
    private byte[] data;

    // The cached serializable instance
    private transient Serializable value;

    public GameSessionDataWrapper()
    {
        this.data = null;
        this.value = null;
    }

    public GameSessionDataWrapper(Serializable value)
    {
        setValue(value);
    }

    public void setValue(Serializable value)
    {
        // Cache
        this.value = value;

        // Convert to actual raw data
        this.data = SerializationUtils.serialize(value);
    }

    public Serializable getValue()
    {
        // Check if value cached, else read raw data...
        if (value == null && data != null)
        {
            try
            {
                value = SerializationUtils.deserialize(data);
            }
            catch (SerializationException e)
            {
                LOG.info("failed to load game session data - class not found - " + e.getMessage());
            }
            catch (Exception e)
            {
                LOG.warn("failed to load game session data", e);
                value = null;
            }
        }

        return value;
    }

}
