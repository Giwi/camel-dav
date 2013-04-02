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
import org.junit.Test;

/**
 * The Class DavConsumerDualDoneFileNameTest.
 * 
 * @version
 */
public class DavConsumerDualDoneFileNameTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/done?initialDelay=0&delay=100";
    }

    /**
     * Test two done file.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testTwoDoneFile() throws Exception {
	getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder(
		"Hello World", "Bye World");

	template.sendBodyAndHeader(getDavUrl()
		+ "&doneFileName=${file:name}.ready", "Hello World",
		Exchange.FILE_NAME, "hello.txt");
	template.sendBodyAndHeader(getDavUrl()
		+ "&doneFileName=${file:name}.ready", "Bye World",
		Exchange.FILE_NAME, "bye.txt");

	assertMockEndpointsSatisfied();
    }

    /**
     * Test one done file missing.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testOneDoneFileMissing() throws Exception {
	getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");

	template.sendBodyAndHeader(getDavUrl()
		+ "&doneFileName=${file:name}.ready", "Hello World",
		Exchange.FILE_NAME, "hello.txt");
	template.sendBodyAndHeader(getDavUrl(), "Bye World",
		Exchange.FILE_NAME, "bye.txt");

	// give chance to poll 2nd file but it lacks the done file
	Thread.sleep(1000);

	assertMockEndpointsSatisfied();
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
		from(getDavUrl() + "&doneFileName=${file:name}.ready")
			.convertBodyTo(String.class).to("mock:result");
	    }
	};
    }

}
