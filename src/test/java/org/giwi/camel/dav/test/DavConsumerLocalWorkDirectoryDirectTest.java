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
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.IOConverter;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavConsumerLocalWorkDirectoryDirectTest.
 * 
 * @version
 */
public class DavConsumerLocalWorkDirectoryDirectTest extends AbstractDavTest {

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
	NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
	assertTrue("Should process one file", notify.matchesMockWaitTime());

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
		from(getDavUrl()).to("file://tmpOut/out");
	    }
	};
    }

}
