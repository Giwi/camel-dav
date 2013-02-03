/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.giwi.camel.dav.test;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test based on end user problem with SFTP on Windows
 */
public class FromDavMoveFileToHiddenFolderRecursiveTest extends AbstractDavTest {

	protected String getDavUrl() {
		return DAV_URL + "/movefile?recursive=true&move=${file:parent}/.done/${file:onlyname}&initialDelay=3000&delay=5000";
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		prepareDavServer();
	}

	@Test
	public void testPollFileAndShouldBeMoved() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedBodiesReceivedInAnyOrder("Hello", "Bye", "Goodday");
		mock.expectedFileExists(DAV_ROOT_DIR + "/.done/hello.txt");
		mock.expectedFileExists(DAV_ROOT_DIR + "/movefile/.done/bye.txt");
		mock.expectedFileExists(DAV_ROOT_DIR + "/movefile/.done/goodday.txt");

		mock.assertIsSatisfied();
	}

	private void prepareDavServer() throws Exception {
		template.sendBodyAndHeader(getDavUrl(), "Hello", Exchange.FILE_NAME, "hello.txt");
		template.sendBodyAndHeader(getDavUrl(), "Bye", Exchange.FILE_NAME, "bye/bye.txt");
		template.sendBodyAndHeader(getDavUrl(), "Goodday", Exchange.FILE_NAME, "goodday/goodday.txt");
	}

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