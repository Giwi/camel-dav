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

import org.apache.camel.Exchange;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to verify shutdown.
 */
public class DavShutdownCompleteCurrentTaskOnlyTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/pending?initialDelay=5000";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
	prepareDavServer();
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	// prepares the DAV Server by creating files on the server that we want
	// to unit
	String davUrl = DAV_URL + "/pending/?password=admin";
	template.sendBodyAndHeader(davUrl, "A", Exchange.FILE_NAME, "a.txt");
	template.sendBodyAndHeader(davUrl, "B", Exchange.FILE_NAME, "b.txt");
	template.sendBodyAndHeader(davUrl, "C", Exchange.FILE_NAME, "c.txt");
	template.sendBodyAndHeader(davUrl, "D", Exchange.FILE_NAME, "d.txt");
	template.sendBodyAndHeader(davUrl, "E", Exchange.FILE_NAME, "e.txt");
    }

    /**
     * Test shutdown complete current task only.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testShutdownCompleteCurrentTaskOnly() throws Exception {
	// give it 20 seconds to shutdown
	context.getShutdownStrategy().setTimeout(20);

	MockEndpoint bar = getMockEndpoint("mock:bar");
	bar.expectedMinimumMessageCount(1);
	Thread.sleep(50);

	assertMockEndpointsSatisfied();

	// shutdown during processing
	context.stop();

	// should NOT route all 5
	assertTrue(
		"Should NOT complete all messages, was: "
			+ bar.getReceivedCounter(),
		bar.getReceivedCounter() < 5);
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
		from(getDavUrl())
			.routeId("route1")
			// let it complete only current task so we shutdown
			// faster
			.shutdownRunningTask(
				ShutdownRunningTask.CompleteCurrentTaskOnly)
			.delay(1000).to("seda:foo");

		from("seda:foo").routeId("route2").to("mock:bar");
	    }
	};
    }
}