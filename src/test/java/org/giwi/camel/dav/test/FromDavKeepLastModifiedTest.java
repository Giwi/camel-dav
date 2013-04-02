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
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to test keepLastModified option.
 */
public class FromDavKeepLastModifiedTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/keep?noop=true";
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
	template.sendBodyAndHeader(getDavUrl(), "Hello World", "CamelFileName",
		"hello.txt");
    }

    /**
     * Test keep last modified.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testKeepLastModified() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).delay(3000).to(
			"file:src/main/data/keep/out?keepLastModified=true",
			"mock:result");
	    }
	});
	context.start();

	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedFileExists("src/main/data/keep/out/hello.txt");
	mock.message(0).header(Exchange.FILE_LAST_MODIFIED).isNotNull();

	assertMockEndpointsSatisfied();

	long t1 = mock.getReceivedExchanges().get(0).getIn()
		.getHeader(Exchange.FILE_LAST_MODIFIED, Date.class).getTime();
	long t2 = new File("src/main/data/keep/out/hello.txt").lastModified();

	assertEquals("Timestamp should have been kept", t1, t2);
    }

    /**
     * Test do not keep last modified.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDoNotKeepLastModified() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).delay(3000).to(
			"file:tmpOut/keep/out?keepLastModified=false",
			"mock:result");
	    }
	});
	context.start();

	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedFileExists("tmpOut/keep/out/hello.txt");
	mock.message(0).header(Exchange.FILE_LAST_MODIFIED).isNotNull();

	assertMockEndpointsSatisfied();

	long t1 = mock.getReceivedExchanges().get(0).getIn()
		.getHeader(Exchange.FILE_LAST_MODIFIED, Date.class).getTime();
	long t2 = new File("tmpOut/keep/out/hello.txt").lastModified();

	assertNotSame("Timestamp should NOT have been kept", t1, t2);
    }

    /**
     * Test do not keep last modified is default.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDoNotKeepLastModifiedIsDefault() throws Exception {
	context.addRoutes(new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).delay(3000).to("file:tmpOut/keep/out",
			"mock:result");
	    }
	});
	context.start();

	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedFileExists("tmpOut/keep/out/hello.txt");
	mock.message(0).header(Exchange.FILE_LAST_MODIFIED).isNotNull();

	assertMockEndpointsSatisfied();

	long t1 = mock.getReceivedExchanges().get(0).getIn()
		.getHeader(Exchange.FILE_LAST_MODIFIED, Date.class).getTime();
	long t2 = new File("tmpOut/keep/out/hello.txt").lastModified();

	assertNotSame("Timestamp should NOT have been kept", t1, t2);
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
}