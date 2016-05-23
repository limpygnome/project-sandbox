package com.projectsandbox.components.server.util.counters;

import java.lang.annotation.Annotation;

/**
 * Created by limpygnome on 07/07/15.
 */
public class AnnotationInfo
{
    private Annotation annotation;
    private Class clazz;

    public AnnotationInfo(Annotation annotation, Class clazz)
    {
        this.annotation = annotation;
        this.clazz = clazz;
    }

    public Annotation getAnnotation()
    {
        return annotation;
    }

    public Class getClazz()
    {
        return clazz;
    }

    /**
     * Retrieves an annotation value.
     *
     * @param key The key to retrieve
     * @return The value, or null if it cannot be accessed/does not exist.
     */
    public Object getAnnotationValue(String key)
    {
        try
        {
            return annotation.annotationType().getMethod(key).invoke(annotation);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
