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
 * The Class DavProducerAllowNullBodyFileAlreadyExistTest.
 */
public class DavProducerAllowNullBodyFileAlreadyExistTest extends
	AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/allow";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    public void setUp() throws Exception {
	super.setUp();
	template.sendBodyAndHeader(getDavUrl(), "Hello world",
		Exchange.FILE_NAME, "hello.txt");
    }

    /**
     * Test file exist append allow null body.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testFileExistAppendAllowNullBody() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:appendTypeAppendResult");
	mock.expectedMessageCount(1);
	mock.expectedFileExists(DAV_ROOT_DIR + "/allow/hello.txt",
		"Hello world");

	template.sendBody("direct:appendTypeAppend", null);

	assertMockEndpointsSatisfied();
    }

    /**
     * Test file exist override allow null body.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testFileExistOverrideAllowNullBody() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:appendTypeOverrideResult");
	mock.expectedMessageCount(1);
	mock.expectedFileExists(DAV_ROOT_DIR + "/allow/hello.txt", "");

	template.sendBody("direct:appendTypeOverride", null);

	assertMockEndpointsSatisfied();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
     */
    @Override
    protected RouteBuilder createRouteBuilder() {
	return new RouteBuilder() {
	    @Override
	    public void configure() {
		from("direct:appendTypeAppend")
			.setHeader(Exchange.FILE_NAME, constant("hello.txt"))
			.to(getDavUrl()
				+ "?allowNullBody=true&fileExist=Append")
			.to("mock:appendTypeAppendResult");

		from("direct:appendTypeOverride")
			.setHeader(Exchange.FILE_NAME, constant("hello.txt"))
			.to(getDavUrl()
				+ "?allowNullBody=true&fileExist=Override")
			.to("mock:appendTypeOverrideResult");
	    }
	};
    }

}