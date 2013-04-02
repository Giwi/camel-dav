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
import org.junit.Test;

/**
 * Unit test for the idempotent=true option.
 */
public class DavConsumerIdempotentTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/idempotent?idempotent=true&delete=true&delay=1000";
    }

    /**
     * Test idempotent.
     * 
     * @throws Exception
     *             the exception
     */
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