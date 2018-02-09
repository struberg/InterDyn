/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.struberg.devtools.cdi.interdyn;

import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class WrappedAnnotatedType<X> implements AnnotatedType<X>
{
    private AnnotatedType wrappedAnnotatedType;
    private Set<Annotation> annotations;

    public WrappedAnnotatedType(AnnotatedType wrappedAnnotatedType)
    {
        this.wrappedAnnotatedType = wrappedAnnotatedType;
        this.annotations = new HashSet<Annotation>(wrappedAnnotatedType.getAnnotations().size());
        this.annotations.addAll(wrappedAnnotatedType.getAnnotations());
    }

    public Class getJavaClass()
    {
        return wrappedAnnotatedType.getJavaClass();
    }

    public Set getConstructors()
    {
        return wrappedAnnotatedType.getConstructors();
    }

    public Set getMethods()
    {
        return wrappedAnnotatedType.getMethods();
    }

    public Set getFields()
    {
        return wrappedAnnotatedType.getFields();
    }

    public Type getBaseType()
    {
        return wrappedAnnotatedType.getBaseType();
    }

    public Set<Type> getTypeClosure()
    {
        return wrappedAnnotatedType.getTypeClosure();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType)
    {
        for (Annotation ann: annotations)
        {
            if (ann.annotationType().equals(annotationType))
            {
                return (T) ann;
            }
        }

        return null;
    }

    public Set<Annotation> getAnnotations()
    {
        return annotations;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
    {
        for (Annotation ann: annotations)
        {
            if (ann.annotationType().equals(annotationType))
            {
                return true;
            }
        }
        return false;
    }
}
