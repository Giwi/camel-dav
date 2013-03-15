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
package org.giwi.camel.dav.test;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.IdempotentRepository;
import org.junit.Test;

/**
 * Unit test for the idempotentRepository # option.
 */
public class DavConsumerIdempotentRefTest extends AbstractDavTest {

    private static boolean invoked;

    private String getDavUrl() {
	return DAV_URL
		+ "/idempotent?idempotent=true&idempotentRepository=#myRepo&delete=true";
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
	JndiRegistry jndi = super.createRegistry();
	jndi.bind("myRepo", new MyIdempotentRepository());
	return jndi;
    }

    @Test
    public void testIdempotent() throws Exception {
	// consume the file the first time
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Hello World");
	mock.expectedMessageCount(1);

	sendFile(getDavUrl(), "Hello World", "report.txt");

	assertMockEndpointsSatisfied();

	Thread.sleep(100);

	// reset mock and set new expectations
	mock.reset();
	mock.expectedMessageCount(0);

	// move file back
	sendFile(getDavUrl(), "Hello World", "report.txt");

	// should NOT consume the file again, let 2 secs pass to let the
	// consumer try to consume it but it should not
	Thread.sleep(2000);
	assertMockEndpointsSatisfied();

	assertTrue("MyIdempotentRepository should have been invoked", invoked);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).to("mock:result");
	    }
	};
    }

    public class MyIdempotentRepository implements IdempotentRepository<String> {

	@Override
	public boolean add(String messageId) {
	    // will return true 1st time, and false 2nd time
	    boolean result = invoked;
	    invoked = true;
	    assertEquals("report.txt", messageId);
	    return !result;
	}

	@Override
	public boolean contains(String key) {
	    return invoked;
	}

	@Override
	public boolean remove(String key) {
	    return true;
	}

	@Override
	public boolean confirm(String key) {
	    return true;
	}

	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}
    }
}