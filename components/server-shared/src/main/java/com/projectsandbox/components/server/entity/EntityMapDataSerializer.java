package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.json.simple.JSONObject;

/**
 * To be implemented by entities requiring custom data, per an instance, to be serialized/deserialized.
 */
public interface EntityMapDataSerializer
{

    void serialize(Controller controller, WorldMap map, JSONObject entityData);

    void deserialize(Controller controller, WorldMap map, JSONObject entityData);

}
