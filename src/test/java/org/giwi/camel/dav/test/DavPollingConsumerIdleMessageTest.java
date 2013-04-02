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

import java.io.File;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Test to verify that the polling consumer delivers an empty Exchange when the
 * sendEmptyMessageWhenIdle property is set and a polling event yields no
 * results.
 */
public class DavPollingConsumerIdleMessageTest extends AbstractDavTest {

    /**
     * Test consume idle messages.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testConsumeIdleMessages() throws Exception {
	Thread.sleep(110);
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMinimumMessageCount(2);
	assertMockEndpointsSatisfied();
	assertTrue(mock.getExchanges().get(0).getIn().getBody() == null);
	assertTrue(mock.getExchanges().get(1).getIn().getBody() == null);
    }

    /**
     * Setup.
     * 
     * @throws Exception
     *             the exception
     */
    @Before
    public void setup() throws Exception {
	new File(DAV_ROOT_DIR + "/polling").mkdirs();
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
		from(
			DAV_URL
				+ "/polling?delay=50&sendEmptyMessageWhenIdle=true")
			.to("mock:result");
	    }
	};
    }

}
