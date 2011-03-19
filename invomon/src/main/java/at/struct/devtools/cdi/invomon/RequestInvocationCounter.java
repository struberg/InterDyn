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
package at.struct.devtools.cdi.invomon;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This bean will get used to count invocations for a single request
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@RequestScoped
public class RequestInvocationCounter
{
    private @Inject Event<MonitorResultEvent> mre;

    @PreDestroy
    public void postUsage()
    {
        mre.fire(new MonitorResultEvent(methodInvocations, classInvocations, methodDurations));
    }


    /**
     * Counter for all method invocations
     * key = fully qualified method name (includes class)
     * value = Integer with value
     */
    private Map<String, AtomicInteger> methodInvocations = new HashMap<String, AtomicInteger>();

    /**
     * Duration of all method invocations
     * key = fully qualified method name (includes class)
     * value = Integer with value
     */
    private Map<String, AtomicLong> methodDurations = new HashMap<String, AtomicLong>();

    /**
     * Counter for all class invocations
     * key = fully qualified class name
     * value = Integer with value
     */
    private Map<String, AtomicInteger> classInvocations  = new HashMap<String, AtomicInteger>();


    /**
     * increment the respective counters
     * @param className
     * @param methodName
     * @param duration
     */
    public void count(String className, String methodName, long duration)
    {
        AtomicInteger classCount = classInvocations.get(className);
        if (classCount == null)
        {
            classCount = new AtomicInteger(0);
            classInvocations.put(className, classCount);
        }
        classCount.incrementAndGet();


        String methodKey = className + "#" + methodName;

        AtomicInteger methCount = methodInvocations.get(methodKey);
        if (methCount == null)
        {
            methCount = new AtomicInteger(0);
            methodInvocations.put(methodKey, methCount);
        }
        methCount.incrementAndGet();

        AtomicLong methDur = methodDurations.get(methodKey);
        if (methDur == null)
        {
            methDur = new AtomicLong(0);
            methodDurations.put(methodKey, methDur);
        }
        methDur.addAndGet(duration);
    }
}
