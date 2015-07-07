package com.limpygnome.projectsandbox.server.utils.counters;

import java.lang.annotation.Annotation;

/**
 * Created by limpygnome on 07/07/15.
 */
public class AnnotationInfo
{
    private Class classAnnotation;
    private Class classAnnotated;

    public AnnotationInfo(Class classAnnotation, Class classAnnotated)
    {
        this.classAnnotation = classAnnotation;
        this.classAnnotated = classAnnotated;
    }

    public Class getClassAnnotation()
    {
        return classAnnotation;
    }

    public Class getClassAnnotated()
    {
        return classAnnotated;
    }

    /**
     * Retrieves an annotation value.
     *
     * @return The value, or null if it cannot be accessed/does not exist.
     */
    public Object getAnnotationValue()
    {
        Annotation annotationInstance = classAnnotated.getAnnotation(classAnnotation);

        try
        {
            return classAnnotation.getMethod("typeId").invoke(annotationInstance);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
