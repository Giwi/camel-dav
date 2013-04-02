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

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.util.FileUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavConsumerLocalWorkDirectoryTest.
 * 
 * @version
 */
public class DavConsumerLocalWorkDirectoryTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL
		+ "/lwd/?delay=5000&localWorkDirectory=tmpOut/lwd&noop=true";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
	deleteDirectory("tmpOut/lwd");
	deleteDirectory("tmpOut/out");
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
	// prepares the DAV Server by creating a file on the server that we want
	// to unit
	// test that we can pool
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Exchange exchange = endpoint.createExchange();
	exchange.getIn().setBody("Hello World");
	exchange.getIn().setHeader(Exchange.FILE_NAME, "hello.txt");
	Producer producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();
    }

    /**
     * Test local work directory.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testLocalWorkDirectory() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Hello World");
	mock.expectedMessageCount(1);

	assertMockEndpointsSatisfied();

	// give test some time to close file resources
	Thread.sleep(6000);

	// and the out file should exists
	File out = new File("tmpOut/out/hello.txt");
	assertTrue("file should exists", out.exists());
	assertEquals("Hello World", IOConverter.toString(out, null));

	// now the lwd file should be deleted
	File local = new File("tmpOut/lwd/hello.txt");
	assertFalse("Local work file should have been deleted", local.exists());
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
		from(getDavUrl()).process(new Processor() {
		    @Override
		    public void process(Exchange exchange) throws Exception {
			File body = exchange.getIn().getBody(File.class);
			assertNotNull(body);
			assertTrue("Local work file should exists",
				body.exists());
			assertEquals(
				FileUtil.normalizePath("tmpOut/lwd/hello.txt"),
				body.getPath());
		    }
		}).to("mock:result", "file://tmpOut/out");
	    }
	};
    }

}
