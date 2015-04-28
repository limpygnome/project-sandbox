package com.limpygnome.projectsandbox.server.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 *
 * @author limpygnome
 */
public final class Annotations
{    
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
                    System.out.println("Annotations - " + annotationType.getName() + " - added type " + clazz.getName());
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
