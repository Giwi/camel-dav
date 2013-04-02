/**
 *  Copyright 2013 Giwi Softwares (http://giwi.free.fr)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.giwi.camel.dav.test;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.giwi.camel.dav.RemoteFilePollingConsumerPollStrategy;
import org.junit.Test;

/**
 * Simulate network issues by using a custom poll strategy to force exceptions
 * occurring during poll.
 * 
 * @version
 */
public class FromDavSimulateNetworkIssueRecoverTest extends AbstractDavTest {

    /** The counter. */
    private static int counter;

    /** The rollback. */
    private static int rollback;

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/recover?pollStrategy=#myPoll";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRegistry()
     */
    @Override
    protected JndiRegistry createRegistry() throws Exception {
	JndiRegistry jndi = super.createRegistry();
	jndi.bind("myPoll", new MyPollStrategy());
	return jndi;
    }

    /**
     * Test dav recover.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDavRecover() throws Exception {
	// should be able to download the file after recovering
	MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
	resultEndpoint.expectedMinimumMessageCount(3);

	template.sendBody(getDavUrl(), "Hello World");

	resultEndpoint.assertIsSatisfied();

	Thread.sleep(2000);

	assertTrue("Should have tried at least 3 times was " + counter,
		counter >= 3);
	assertEquals(2, rollback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).to("mock:result");
	    }
	};
    }

    /**
     * The Class MyPollStrategy.
     */
    public class MyPollStrategy extends RemoteFilePollingConsumerPollStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.camel.impl.DefaultPollingConsumerPollStrategy#commit(org
	 * .apache.camel.Consumer, org.apache.camel.Endpoint, int)
	 */
	@Override
	public void commit(Consumer consumer, Endpoint endpoint,
		int polledMessages) {
	    counter++;
	    if (counter < 3) {
		throw new IllegalArgumentException("Forced by unit test");
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.giwi.camel.dav.RemoteFilePollingConsumerPollStrategy#rollback
	 * (org.apache.camel.Consumer, org.apache.camel.Endpoint, int,
	 * java.lang.Exception)
	 */
	@Override
	public boolean rollback(Consumer consumer, Endpoint endpoint,
		int retryCounter, Exception e) throws Exception {
	    rollback++;
	    return super.rollback(consumer, endpoint, retryCounter, e);
	}
    }
}