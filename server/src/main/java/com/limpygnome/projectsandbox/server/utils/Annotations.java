package com.limpygnome.projectsandbox.server.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 *
 * @author limpygnome
 */
public final class Annotations
{
    private final static Logger LOG = LogManager.getLogger(Annotations.class);

    /**
     * Finds all of the classes annotated within a package, including
     * sub-packages.
     * 
     * @param annotationType
     * @param classPath
     * @return
     * @throws Exception 
     */
    public static HashMap<Short, Class> findAnnotatedClasses(Class annotationType, String classPath) throws Exception
    {
        HashMap<Short, Class> result = new HashMap<>();
        
        // Iterate all classes to find Entity classes and create map to type ID
        // TODO: support for multiple paths
        Class[] classes = FileSystem.getAllClasses(classPath);
        
        Annotation annotationInstance;
        short typeId;
        
        for (Class clazz : classes)
        {
            if (clazz.isAnnotationPresent(annotationType))
            {
                // Read annotation
                annotationInstance = clazz.getAnnotation(annotationType);
                
                /// Retrieve typeId
                typeId = (short) annotationType.getMethod("typeId").invoke(annotationInstance);
                
                if (result.containsKey(typeId))
                {
                    throw new IllegalArgumentException("Annotations - " + annotationType.getName() + " - " + clazz.getName() + " - non-unique ID of " + typeId);
                }
                else if (typeId != 0)
                {
                    result.put(typeId, clazz);
                    LOG.debug("{} - added type: {}", annotationType.getName(), clazz.getName());
                }
                else if (!Modifier.isAbstract(clazz.getModifiers()))
                {
                    throw new IllegalArgumentException("Annotations - " + annotationType.getName() + " - " + clazz.getName() + " - invalid type identifier");
                }
            }
        }
        
        return result;
    }
}
