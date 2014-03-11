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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test that ftp consumer will not match directories (CAMEL-920).
 */
public class DavConsumerDirectoriesNotMatchedTest extends AbstractDavTest {

	/**
	 * Gets the dav url.
	 * 
	 * @return the dav url
	 */
	private String getDavUrl() {
		return DAV_URL + "/dirnotmatched/?recursive=true&include=^.*txt$";
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
	 * Test skip directories.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSkipDirectories() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(3);
		mock.assertIsSatisfied();
	}

	/**
	 * Prepare dav server.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void prepareDavServer() throws Exception {
		// prepares the DAV Server by creating files on the server that we want
		// to unit test that we can pool and store as a local file

		sendFile(getDavUrl(), "This is a dot file", ".skipme");
		sendFile(getDavUrl(), "This is a web file", "index.html");
		sendFile(getDavUrl(), "This is a readme file", "readme.txt");
		sendFile(getDavUrl() + "/2007/", "2007 report", "report2007.txt");
		sendFile(getDavUrl() + "/2008/", "2008 report", "report2008.txt");
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