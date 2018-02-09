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


import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>InterDyn is a CDI (JSR-299) Extension for dynamically
 * attaching CDI interceptors to a class.</p>
 *
 * <p>Just plug it into your classpath and create a
 * META-INF/struct/interdyn.properties file with the following content</p>
 * <pre>
 * enabled=true
 * rule.1.match=[regular expression which fits the fully qualified class name]
 * rule.1.interceptor=[fully qualified interceptor annotation name]
 * rule.2.match...
 * </pre>
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */

public class InterDynExtension implements Extension
{
    private List<InterceptorRule> interceptorRules = new ArrayList<InterceptorRule>();

    private static final String INTERCEPTOR_CONFIG_FILE = "META-INF/struct/interdyn.properties";

    private Logger logger = Logger.getLogger(InterDynExtension.class.getName());

    private Map<String, Annotation> usedInterceptorBindings = new HashMap<String, Annotation>();

    private boolean inited = false;
    private boolean enabled = false;

    public void init()
    {
        if (inited)
        {
            return;
        }
        ClassLoader cl = getCurrentClassLoader();

        URL propertyUrl = cl.getResource(INTERCEPTOR_CONFIG_FILE);

        if (propertyUrl != null)
        {
            try
            {
                InputStream is = propertyUrl.openStream();
                Properties prop = new Properties();
                prop.load(is);
                String val = prop.getProperty("enabled");

                if ("false".equals(val))
                {
                    return;
                }


                int i = 1;

                String match;
                String interceptorBindingClassName;
                do
                {
                    match = prop.getProperty( "rule." + i + ".match");
                    interceptorBindingClassName = prop.getProperty( "rule." + i + ".interceptor");

                    if (match != null && interceptorBindingClassName != null)
                    {
                        Annotation interceptorBinding = getInterceptorBinding(interceptorBindingClassName);
                        interceptorRules.add(new InterceptorRule(match, interceptorBinding));
                    }

                    i++;
                } while (match != null && interceptorBindingClassName != null);


                if (!interceptorRules.isEmpty())
                {
                    enabled = true;
                }

            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "Error opening " + INTERCEPTOR_CONFIG_FILE, e);
            }

        }

        inited = true;
    }

    public void processAnnotatedType(@Observes ProcessAnnotatedType pat)
    {
        init();

        if (enabled)
        {
            String beanClassName = pat.getAnnotatedType().getJavaClass().getName();
            AnnotatedType at = pat.getAnnotatedType();

            WrappedAnnotatedType wrappedAnnotatedType = null;
            for (InterceptorRule rule : interceptorRules)
            {
                if (beanClassName.matches(rule.getRule()))
                {
                    if (wrappedAnnotatedType == null)
                    {
                        wrappedAnnotatedType = new WrappedAnnotatedType(at);
                    }
                    wrappedAnnotatedType.getAnnotations().add(rule.getInterceptorBinding());
                    logger.info("Adding Dynamic Interceptor " + rule.getInterceptorBinding() + " to class " + beanClassName );
                }
            }
            if (wrappedAnnotatedType != null)
            {
                pat.setAnnotatedType(wrappedAnnotatedType);
            }
        }

    }


    private ClassLoader getCurrentClassLoader()
    {
        ClassLoader loader =  Thread.currentThread().getContextClassLoader();

        if (loader == null)
        {
            loader = InterDynExtension.class.getClassLoader();
        }

        return loader;
    }

    private Annotation getInterceptorBinding(String interceptorBindingClassName)
    {
        Annotation ann = usedInterceptorBindings.get(interceptorBindingClassName);

        if (ann == null)
        {
            Class<? extends Annotation> annClass;
            try
            {
                annClass = (Class<? extends Annotation>) getCurrentClassLoader().loadClass(interceptorBindingClassName);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Error while picking up dynamic InterceptorBindingType for class" +
                                           interceptorBindingClassName, e);
            }
            ann = DefaultAnnotation.of(annClass);
            usedInterceptorBindings.put(interceptorBindingClassName, ann);
        }
        return ann;
    }


}
