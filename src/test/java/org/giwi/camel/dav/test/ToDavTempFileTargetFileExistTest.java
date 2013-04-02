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
 * The Class ToDavTempFileTargetFileExistTest.
 */
public class ToDavTempFileTargetFileExistTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL
		+ "/tempfile?fileName=./foo/bar/message.txt&tempFileName=${file:onlyname.noext}.tmp";
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
     * Test send file target file exist.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testSendFileTargetFileExist() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedBodiesReceived("Hello Again World");
	mock.expectedFileExists(DAV_ROOT_DIR + "/tempfile/foo/bar/message.txt",
		"Hello Again World");

	template.sendBody("direct:start", "Hello Again World");

	mock.assertIsSatisfied();
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	// prepares the DAV Server by creating a file on the server
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Exchange exchange = endpoint.createExchange();
	exchange.getIn().setBody("Hello World");
	exchange.getIn().setHeader(Exchange.FILE_NAME, "foo/bar/message.txt");
	Producer producer = endpoint.createProducer();
	producer.start();
	producer.process(exchange);
	producer.stop();

	// assert file is created
	File file = new File(DAV_ROOT_DIR + "/tempfile/foo/bar/message.txt");
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
		from("direct:start").to(getDavUrl()).to("mock:result");
	    }
	};
    }
}