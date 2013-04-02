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
 * The Class DavConsumerRelativeFileNameTest.
 */
public class DavConsumerRelativeFileNameTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL
		+ "/tmpOut/filename-consumer?recursive=true&sortBy=file:name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    public void setUp() throws Exception {
	super.setUp();
	sendFile(getDavUrl(), "Hello World",
		"tmpOut/filename-consumer-hello.txt");
	sendFile(getDavUrl(), "Bye World", "tmpOut/filename-consumer-bye.txt");
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
     * Test valid filename on exchange.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testValidFilenameOnExchange() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(2);
	// should have file name header set
	mock.allMessages().header(Exchange.FILE_NAME).isNotNull();

	assertMockEndpointsSatisfied();

	// give time for ftp consumer to disconnect
	Thread.sleep(2000);

	assertDirectoryEquals(
		"tmpOut/filename-consumer-bye.txt",
		mock.getReceivedExchanges().get(0).getIn()
			.getHeader(Exchange.FILE_NAME, String.class));
	assertDirectoryEquals(
		"tmpOut/filename-consumer-hello.txt",
		mock.getReceivedExchanges().get(1).getIn()
			.getHeader(Exchange.FILE_NAME, String.class));
    }

}