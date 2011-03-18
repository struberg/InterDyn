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

import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * This class will observe all {@link MonitorResultEvent}s
 * and log them accordingly
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class InvocationResultLogger
{
    private static final Logger logger = Logger.getLogger(InvocationResultLogger.class.getName());

    private static final int MAX_LOG_LINES = 8;

    public void logMonitorResultEvents(@Observes MonitorResultEvent mre)
    {
        // we copy them because we don't like to make the event data dirty.
        // there might be other observers interested in the result...
        List<ResultEntry> methodInvocations = createResultEntries(mre.getMethodInvocations());
        List<ResultEntry> classInvocations  = createResultEntries(mre.getClassInvocations());

        StringBuilder sb = new StringBuilder();
        sb.append("Top Class Invocations:\n");
        for (int i=1; i < MAX_LOG_LINES && i< classInvocations.size(); i++)
        {
            ResultEntry re = classInvocations.get(classInvocations.size() - i);
            sb.append("  ").append(re.getCount()).append("\t").append(re.getToken()).append("\n");
        }
        logger.info(sb.toString());

        sb = new StringBuilder();
        sb.append("Top Method Invocations:\n");
        for (int i=1; i < MAX_LOG_LINES && i< methodInvocations.size(); i++)
        {
            ResultEntry re = methodInvocations.get(methodInvocations.size() - i);
            sb.append("  ").append(re.getCount()).append("\t").append(re.getToken()).append("\n");
        }
        logger.info(sb.toString());
    }


    private List<ResultEntry> createResultEntries(Map<String, AtomicInteger> invocations)
    {
        List<ResultEntry> resultEntries = new ArrayList<ResultEntry>(invocations.size());

        for (Map.Entry<String, AtomicInteger> entry : invocations.entrySet())
        {
            resultEntries.add(new ResultEntry(entry.getValue().intValue(), entry.getKey()));
        }

        Collections.sort(resultEntries);

        return resultEntries;
    }

    private static class ResultEntry implements Comparable<ResultEntry>
    {
        private Integer count;
        private String token;

        private ResultEntry(Integer count, String token)
        {
            this.count = count;
            this.token = token;
        }

        public Integer getCount()
        {
            return count;
        }

        public String getToken()
        {
            return token;
        }

        public int compareTo(ResultEntry o)
        {
            return count.compareTo(o.count);
        }
    }
}
