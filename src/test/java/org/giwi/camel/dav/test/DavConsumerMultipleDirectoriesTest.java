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
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.giwi.camel.dav.RemoteFile;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavConsumerMultipleDirectoriesTest.
 * 
 * @version
 */
public class DavConsumerMultipleDirectoriesTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL
		+ "/multidir/?recursive=true&consumer.delay=5000&sortBy=file:path";
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
     * Test multi dir.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testMultiDir() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Bye World", "Hello World", "Goodday World");

	assertMockEndpointsSatisfied();

	Exchange exchange = mock.getExchanges().get(0);
	RemoteFile<?> file = (RemoteFile<?>) exchange
		.getProperty(FileComponent.FILE_EXCHANGE_FILE);
	assertNotNull(file);
	assertDirectoryEquals("webdav/multidir/bye.txt",
		file.getAbsoluteFilePath());
	assertDirectoryEquals("bye.txt", file.getRelativeFilePath());
	assertEquals("bye.txt", file.getFileName());

	exchange = mock.getExchanges().get(1);
	file = (RemoteFile<?>) exchange
		.getProperty(FileComponent.FILE_EXCHANGE_FILE);
	assertNotNull(file);
	assertDirectoryEquals("webdav/multidir/sub/hello.txt",
		file.getAbsoluteFilePath());
	assertDirectoryEquals("sub/hello.txt", file.getRelativeFilePath());
	assertEquals("sub/hello.txt", file.getFileName());
	assertEquals("hello.txt", file.getFileNameOnly());

	exchange = mock.getExchanges().get(2);
	file = (RemoteFile<?>) exchange
		.getProperty(FileComponent.FILE_EXCHANGE_FILE);
	assertNotNull(file);
	assertDirectoryEquals("webdav/multidir/sub/sub2/godday.txt",
		file.getAbsoluteFilePath());
	assertDirectoryEquals("sub/sub2/godday.txt", file.getRelativeFilePath());
	assertEquals("sub/sub2/godday.txt", file.getFileName());
	assertEquals("godday.txt", file.getFileNameOnly());
    }

    /**
     * Prepare dav server.
     * 
     * @throws Exception
     *             the exception
     */
    private void prepareDavServer() throws Exception {
	sendFile(getDavUrl(), "Bye World", "bye.txt");
	sendFile(getDavUrl(), "Hello World", "sub/hello.txt");
	sendFile(getDavUrl(), "Goodday World", "sub/sub2/godday.txt");
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