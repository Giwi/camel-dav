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
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavConsumerTemplateTest.
 */
public class DavConsumerTemplateTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/template";
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#isUseRouteBuilder()
     */
    @Override
    public boolean isUseRouteBuilder() {
	return false;
    }

    /**
     * Test consumer template.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testConsumerTemplate() throws Exception {
	Exchange exchange = consumer.receive(getDavUrl(), 5000);
	assertNotNull(exchange);
	assertEquals("hello.txt", exchange.getIn()
		.getHeader(Exchange.FILE_NAME));
	assertEquals("Hello World", exchange.getIn().getBody(String.class));

	// must done when we are done using the exchange
	consumer.doneUoW(exchange);

	Thread.sleep(500);

	// poll the same file again
	exchange = consumer.receive(getDavUrl(), 5000);
	assertNotNull(exchange);
	assertEquals("hello.txt", exchange.getIn()
		.getHeader(Exchange.FILE_NAME));
	assertEquals("Hello World", exchange.getIn().getBody(String.class));

	// must done when we are done using the exchange
	consumer.doneUoW(exchange);

	// file should still exists
	Thread.sleep(500);
	File file = new File(DAV_ROOT_DIR + "/template/hello.txt");
	assertTrue("The file should exist: " + file, file.exists());
    }

    /**
     * Test consumer template not done.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testConsumerTemplateNotDone() throws Exception {
	Exchange exchange = consumer.receive(getDavUrl(), 5000);
	assertNotNull(exchange);
	assertEquals("hello.txt", exchange.getIn()
		.getHeader(Exchange.FILE_NAME));
	assertEquals("Hello World", exchange.getIn().getBody(String.class));

	// forget to call done

	Thread.sleep(500);

	// try poll the same file again
	Exchange exchange2 = consumer.receive(getDavUrl(), 2000);
	assertNull(exchange2);

	// now done the original exchange
	consumer.doneUoW(exchange);

	// now we can poll the file again as we have done the exchange
	exchange2 = consumer.receive(getDavUrl(), 2000);
	assertNotNull(exchange2);
	assertEquals("hello.txt",
		exchange2.getIn().getHeader(Exchange.FILE_NAME));
	assertEquals("Hello World", exchange2.getIn().getBody(String.class));
	consumer.doneUoW(exchange2);

	// file should still exists
	Thread.sleep(500);
	File file = new File(DAV_ROOT_DIR + "/template/hello.txt");
	assertTrue("The file should exist: " + file, file.exists());
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Exchange exchange = endpoint.createExchange();
	exchange.getIn().setBody("Hello World");
	exchange.getIn().setHeader(Exchange.FILE_NAME, "hello.txt");
	Producer producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();

	// assert file is created
	File file = new File(DAV_ROOT_DIR + "/template/hello.txt");
	assertTrue("The file should exist: " + file, file.exists());
    }

}