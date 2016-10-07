package com.projectsandbox.components.server.world.map.type;

import com.projectsandbox.components.server.world.map.WorldMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate active map classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface MapType
{
    String typeName();

    Class<? extends WorldMap> classType();
}
