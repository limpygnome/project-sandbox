package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Implemented by instances of map data, which are expected to be serializable to JSON.
 */
public interface MapData
{

    /**
     * Can be invoked at any moment when data needs to be serialized and persisted.
     *
     * @param controller the current controller
     * @param map the current map to which the data belongs
     * @param root the root object of which to attach custom data
     * @throws IOException thrown when unable to serialize
     */
    void serialize(Controller controller, WorldMap map, JSONObject root) throws IOException;

    /**
     * Invoked when a new instance of this class is made.
     *
     * @param controller the current controller
     * @param map the current map to which the data belongs
     * @param root root object with custom data to be read into this instance
     * @throws IOException thrown when unable to deserialize
     */
    void deserialize(Controller controller, WorldMap map, JSONObject root) throws IOException;

}
