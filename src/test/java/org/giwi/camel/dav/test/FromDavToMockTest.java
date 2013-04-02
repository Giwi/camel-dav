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
 * The Class FromDavToMockTest.
 * 
 * @version
 */
public class FromDavToMockTest extends AbstractDavTest {

    /** The expected body. */
    protected String expectedBody = "Hello there!";

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/tmp/camel?recursive=true";
    }

    /**
     * Test dav route.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDavRoute() throws Exception {
	MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
	resultEndpoint.expectedBodiesReceived(expectedBody);

	template.sendBodyAndHeader(getDavUrl(), expectedBody, "cheese", 123);

	resultEndpoint.assertIsSatisfied();
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