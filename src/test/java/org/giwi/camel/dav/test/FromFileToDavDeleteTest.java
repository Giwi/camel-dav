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

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * @version
 */
public class FromFileToDavDeleteTest extends AbstractDavTest {

	@Test
	public void testFromFileToDavDelete() throws Exception {
		NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(1);

		template.sendBodyAndHeader("file:src/main/data", "Hello World", Exchange.FILE_NAME, "hello.txt");

		assertMockEndpointsSatisfied();
		assertTrue(notify.matchesMockWaitTime());

		// file should be deleted
		File file = new File("src/main/data/hello.txt");
		assertFalse("File should be deleted", file.exists());

		// file should exists on ftp server
		file = new File(DAV_ROOT_DIR + "/hello.txt");
		assertTrue("File should exist on dav server", file.exists());
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:src/main/data?delete=true").to(DAV_URL).to("mock:result");
			}
		};
	}
}