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
import org.apache.webbeans.cditest.CdiTestContainer;
import org.apache.webbeans.cditest.CdiTestContainerLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class DynamicInterceptorInvocationTest
{
    private CdiTestContainer containerStarter;

    @Test
    public void testInterceptorInvocation() throws Exception
    {
        try
        {
            containerStarter = CdiTestContainerLoader.getCdiContainer();
            containerStarter.bootContainer();

            Assert.assertEquals(TestInterceptor.invocationCount, 0);

            MyTestRequestBean myRequestBean = containerStarter.getInstance(MyTestRequestBean.class);
            Assert.assertNotNull(myRequestBean);

            int i = myRequestBean.getI();

            Assert.assertEquals(TestInterceptor.invocationCount, 1);
        }
        finally
        {
            containerStarter.shutdownContainer();
        }
    }
}
