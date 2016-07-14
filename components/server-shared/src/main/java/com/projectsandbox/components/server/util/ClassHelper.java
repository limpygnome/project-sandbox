package com.projectsandbox.components.server.util;

import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by limpygnome on 14/07/16.
 */
@Component
public class ClassHelper
{
    private static final String DEFAULT_PACKAGE = "com.projectsandbox";

    public <T extends Class> Set<Class<?>> fetchClassesByAnnotation(T type)
    {
        return fetchClassesByAnnotation(DEFAULT_PACKAGE, false, type);
    }

    public <T extends Class> Set<Class<?>> fetchClassesByAnnotation(String packageToScan, boolean includeAbstract, T type)
    {
        Set<Class<?>> result;

        Reflections reflections = new Reflections(packageToScan);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(type);

        // Apply filters
        if (includeAbstract)
        {
            result = annotatedClasses;
        }
        else
        {
            result = new HashSet<>();
            for (Class annotatedClass : annotatedClasses)
            {
                if (!Modifier.isAbstract(annotatedClass.getModifiers()))
                {
                    result.add(annotatedClass);
                }
            }
        }

        return result;
    }

}
