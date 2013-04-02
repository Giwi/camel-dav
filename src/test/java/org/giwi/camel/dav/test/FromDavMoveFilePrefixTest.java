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
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to test consumer.moveNamePrefix option.
 */
public class FromDavMoveFilePrefixTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/movefile?consumer.delay=5000"
		+ "&move=done/${file:name}";
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
	mock.expectedMessageCount(1);
	mock.expectedBodiesReceived("Hello World this file will be moved");
	mock.expectedFileExists(DAV_ROOT_DIR + "/movefile/done/hello.txt");

	mock.assertIsSatisfied();
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	// prepares the FTP Server by creating a file on the server that we want
	// to unit
	// test that we can pool and store as a local file
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Exchange exchange = endpoint.createExchange();
	exchange.getIn().setBody("Hello World this file will be moved");
	exchange.getIn().setHeader(Exchange.FILE_NAME, "hello.txt");
	Producer producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();

	// assert file is created
	File file = new File(DAV_ROOT_DIR + "/movefile/hello.txt");
	assertTrue("The file should exists", file.exists());
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