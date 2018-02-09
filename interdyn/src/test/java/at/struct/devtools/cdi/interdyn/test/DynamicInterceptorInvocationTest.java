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
package at.struct.devtools.cdi.interdyn.test;

import at.struct.devtools.cdi.interdyn.test.domainobjects.MyTestRequestBean;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@RunWith(CdiTestRunner.class)
public class DynamicInterceptorInvocationTest
{

    private @Inject MyTestRequestBean myRequestBean;

    @Test
    public void testInterceptorInvocation() throws Exception
    {
        TestInterceptor.invocationCount = 0;

        myRequestBean.setI(4711);
        int i = myRequestBean.getI();

        Assert.assertEquals(4711, i);

        Assert.assertEquals(2, TestInterceptor.invocationCount);
    }
}
