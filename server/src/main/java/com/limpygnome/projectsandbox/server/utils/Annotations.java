package com.limpygnome.projectsandbox.server.utils;

import com.limpygnome.projectsandbox.server.utils.counters.AnnotationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author limpygnome
 */
public final class Annotations
{
    private final static Logger LOG = LogManager.getLogger(Annotations.class);

    /**
     * Finds all of the classes annotated within a package, including sub-packages.
     * 
     * @param annotationType
     * @param classPaths
     * @return
     * @throws Exception 
     */
    public static List<AnnotationInfo> findAnnotatedClasses(Class annotationType, String[] classPaths, boolean excludeAbstract) throws Exception
    {
        List<AnnotationInfo> result = new LinkedList<>();
        
        for (String classPath : classPaths)
        {
            findAnnotatedClasses(annotationType, result, classPath, excludeAbstract);
        }
        
        return result;
    }

    private static void findAnnotatedClasses(Class annotationType, List<AnnotationInfo> result, String classPath,
                                                boolean excludeAbstract)
            throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        // Iterate all classes to find Entity classes and create map to type ID
        Class[] classes = FileSystem.getAllClasses(classPath);

        Annotation annotationInstance;

        for (Class clazz : classes)
        {
            if ((!excludeAbstract || !Modifier.isAbstract(clazz.getModifiers())) && clazz.isAnnotationPresent(annotationType))
            {
                // Fetch annotation instance
                annotationInstance = clazz.getAnnotation(annotationType);

                // Add to result
                result.add(new AnnotationInfo(annotationInstance, clazz));
            }
        }
    }

}
