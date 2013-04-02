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
import org.junit.Test;

/**
 * The Class DavRecursiveDepthTest.
 * 
 * @version
 */
public class DavRecursiveDepthTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/depth?recursive=true";
    }

    /**
     * Test depth.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDepth() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceivedInAnyOrder("a2", "b2");

	template.sendBodyAndHeader(DAV_URL + "/depth?password=admin", "a",
		Exchange.FILE_NAME, "a.txt");
	template.sendBodyAndHeader(DAV_URL + "/depth?password=admin", "b",
		Exchange.FILE_NAME, "b.txt");
	template.sendBodyAndHeader(DAV_URL + "/depth/foo?password=admin", "a2",
		Exchange.FILE_NAME, "a2.txt");
	template.sendBodyAndHeader(DAV_URL + "/depth/foo/bar?password=admin",
		"a3", Exchange.FILE_NAME, "a.txt");
	template.sendBodyAndHeader(DAV_URL + "/depth/bar?password=admin", "b2",
		Exchange.FILE_NAME, "b2.txt");
	template.sendBodyAndHeader(DAV_URL + "/depth/bar/foo?password=admin",
		"b3", Exchange.FILE_NAME, "b.txt");

	// only expect 2 of the 6 sent, those at depth 2
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
		from(getDavUrl() + "&minDepth=2&maxDepth=2").convertBodyTo(
			String.class).to("mock:result");
	    }
	};
    }
}
