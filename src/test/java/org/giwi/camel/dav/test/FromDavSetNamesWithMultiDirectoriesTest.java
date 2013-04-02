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
import org.apache.camel.converter.IOConverter;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to verify that using option setNames and having multi remote
 * directories the files are stored locally in the same directory layout.
 */
public class FromDavSetNamesWithMultiDirectoriesTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL
		+ "/incoming?initialDelay=2500&delay=5000&recursive=true";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
	deleteDirectory("tmpOut/davsetnamestest");
	super.setUp();
	prepareDavServer();
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
	resultEndpoint.expectedMessageCount(2);
	resultEndpoint.assertIsSatisfied();
	Exchange ex = resultEndpoint.getExchanges().get(0);
	byte[] bytes = ex.getIn().getBody(byte[].class);
	assertTrue("Logo size wrong", bytes.length > 10000);

	// assert the file
	File file = new File("tmpOut/davsetnamestest/data1/logo1.jpeg");
	assertTrue("The binary file should exists", file.exists());
	assertTrue("Logo size wrong", file.length() > 10000);

	// assert the file
	file = new File("tmpOut/davsetnamestest/data2/logo2.png");
	assertTrue(" The binary file should exists", file.exists());
	assertTrue("Logo size wrong", file.length() > 50000);
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
	// test that we can pool and store as a local file
	String davUrl = DAV_URL + "/incoming/data1/";
	Endpoint endpoint = context.getEndpoint(davUrl);
	Exchange exchange = endpoint.createExchange();
	exchange.getIn().setBody(
		IOConverter.toFile("src/test/data/davbinarytest/logo1.jpg"));
	exchange.getIn().setHeader(Exchange.FILE_NAME, "logo1.jpeg");
	Producer producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();

	davUrl = DAV_URL + "/incoming/data2/";
	endpoint = context.getEndpoint(davUrl);
	exchange = endpoint.createExchange();
	exchange.getIn().setBody(
		IOConverter.toFile("src/test/data/davbinarytest/logo2.png"));
	exchange.getIn().setHeader(Exchange.FILE_NAME, "logo2.png");
	producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();
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
		from(getDavUrl()).to("file:tmpOut/davsetnamestest",
			"mock:result");
	    }
	};
    }
}