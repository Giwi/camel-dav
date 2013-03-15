/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.giwi.camel.dav.test.auth;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.PollingConsumerPollStrategy;
import org.apache.camel.support.ServiceSupport;
import org.giwi.camel.dav.test.AbstractDavTest;
import org.junit.Test;

/**
 * Unit test for login failure due bad password and no re connect attempts
 * allowed
 */
public class DavConsumerThrowExceptionOnLoginFailedTest extends AbstractDavTest {
    // FIXME
    private final CountDownLatch latch = new CountDownLatch(1);

    private String getDavUrl() {
	return "dav://dummy@localhost:80/webdavs/badlogin?password=cantremember&throwExceptionOnConnectFailed=true&maximumReconnectAttempts=0&pollStrategy=#myPoll";
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
	JndiRegistry jndi = super.createRegistry();
	jndi.bind("myPoll", new MyPoll());
	return jndi;
    }

    @Test
    public void testBadLogin() throws Exception {
	getMockEndpoint("mock:result").expectedMessageCount(0);

	assertTrue(latch.await(5, TimeUnit.SECONDS));

	assertMockEndpointsSatisfied();

	// consumer should be stopped
	Thread.sleep(1000);

	Consumer consumer = context.getRoute("foo").getConsumer();
	assertTrue("Consumer should be stopped",
		((ServiceSupport) consumer).isStopped());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).routeId("foo").to("mock:result");
	    }
	};
    }

    private class MyPoll implements PollingConsumerPollStrategy {

	@Override
	public boolean begin(Consumer consumer, Endpoint endpoint) {
	    return true;
	}

	@Override
	public void commit(Consumer consumer, Endpoint endpoint,
		int polledMessages) {
	}

	@Override
	public boolean rollback(Consumer consumer, Endpoint endpoint,
		int retryCounter, Exception cause) throws Exception {
	    GenericFileOperationFailedException e = assertIsInstanceOf(
		    GenericFileOperationFailedException.class, cause);
	    assertEquals(530, e.getCode());

	    // stop the consumer
	    consumer.stop();

	    latch.countDown();

	    return false;
	}
    }
}