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
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.interceptor.Tracer;
import org.junit.Test;

/**
 * An unit test based on Paddy having trouble with SFTP.
 */
public class PaddyRouteTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/paddy/?password=admin&recursive=true";
    }

    /**
     * Test consume file.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testConsumeFile() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);

	sendFile(getDavUrl() + "/?password=admin", "Hello World",
		"incoming/hello.txt");

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
		getContext().addInterceptStrategy(new Tracer());

		from(getDavUrl()).process(new Processor() {
		    public void process(Exchange exchange) throws Exception {
			assertNotNull(exchange.getIn().getHeader(
				Exchange.FILE_NAME));
			assertEquals(
				"hello.txt",
				exchange.getIn().getHeader(
					Exchange.FILE_NAME_ONLY));
			assertEquals("Hello World",
				exchange.getIn().getBody(String.class));
		    }
		}).to("mock:result");
	    }
	};
    }
}