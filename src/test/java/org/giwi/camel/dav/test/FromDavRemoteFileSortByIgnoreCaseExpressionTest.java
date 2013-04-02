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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to verify remotefile sortby option.
 */
public class FromDavRemoteFileSortByIgnoreCaseExpressionTest extends
	AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/sortbyignore?consumer.delay=5000";
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#isUseRouteBuilder()
     */
    @Override
    public boolean isUseRouteBuilder() {
	return false;
    }

    /**
     * Test sort files.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSortFiles() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl() + "&sortBy=file:name").to("mock:result");
	    }
	});
	context.start();

	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Hello London", "Hello Copenhagen",
		"Hello Paris");

	assertMockEndpointsSatisfied();
    }

    /**
     * Test sort files no case.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSortFilesNoCase() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl() + "&sortBy=ignoreCase:file:name").to(
			"mock:nocase");
	    }
	});
	context.start();

	MockEndpoint nocase = getMockEndpoint("mock:nocase");
	nocase.expectedBodiesReceived("Hello Copenhagen", "Hello London",
		"Hello Paris");

	assertMockEndpointsSatisfied();
    }

    /**
     * Test sort files no case reverse.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSortFilesNoCaseReverse() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl() + "&sortBy=reverse:ignoreCase:file:name").to(
			"mock:nocasereverse");
	    }
	});
	context.start();

	MockEndpoint nocasereverse = getMockEndpoint("mock:nocasereverse");
	nocasereverse.expectedBodiesReceived("Hello Paris", "Hello London",
		"Hello Copenhagen");

	assertMockEndpointsSatisfied();
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	// prepares the FTP Server by creating files on the server that we want
	// to unit
	// test that we can pool
	sendFile(getDavUrl(), "Hello Paris", "report-3.dat");
	sendFile(getDavUrl(), "Hello London", "REPORT-2.txt");
	sendFile(getDavUrl(), "Hello Copenhagen", "Report-1.xml");
    }
}