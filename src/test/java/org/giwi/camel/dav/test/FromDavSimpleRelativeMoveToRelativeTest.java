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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class FromDavSimpleRelativeMoveToRelativeTest.
 * 
 * @version
 */
public class FromDavSimpleRelativeMoveToRelativeTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL
		+ "/movefile?recursive=true&move=.done&initialDelay=2500&delay=5000";
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
     * Test poll file and should be moved.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testPollFileAndShouldBeMoved() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceivedInAnyOrder("Hello", "Bye", "Goodday");
	mock.expectedFileExists(DAV_ROOT_DIR + "/movefile/.done/hello.txt");
	mock.expectedFileExists(DAV_ROOT_DIR + "/movefile/sub/.done/bye.txt");
	mock.expectedFileExists(DAV_ROOT_DIR
		+ "/movefile/sub/sub2/.done/goodday.txt");

	mock.assertIsSatisfied();
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	template.sendBodyAndHeader(getDavUrl(), "Hello", Exchange.FILE_NAME,
		"hello.txt");
	template.sendBodyAndHeader(getDavUrl(), "Bye", Exchange.FILE_NAME,
		"sub/bye.txt");
	template.sendBodyAndHeader(getDavUrl(), "Goodday", Exchange.FILE_NAME,
		"sub/sub2/goodday.txt");
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
}