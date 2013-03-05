/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.giwi.camel.dav.test;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * @version
 */
public class DavSimpleConsumeAbsoluteTest extends AbstractDavTest {

	@Test
	public void testDavSimpleConsumeAbsolute() throws Exception {

		String expected = "Hello World";

		// create file using regular file

		// FTP Server does not support absolute path, so lets simulate it
		String path = DAV_ROOT_DIR + "/tmp/mytemp";
		template.sendBodyAndHeader("file:" + path, expected, Exchange.FILE_NAME, "hello.txt");

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(1);
		mock.expectedHeaderReceived(Exchange.FILE_NAME, "hello.txt");

		context.startRoute("foo");

		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// notice we use an absolute starting path: /tmp/mytemp
				// - we must remember to use // slash because of the url
				// separator
				from(DAV_URL + "//tmp/mytemp?delay=10s&disconnect=true").routeId("foo").noAutoStartup().to("mock:result");
			}
		};
	}
}
