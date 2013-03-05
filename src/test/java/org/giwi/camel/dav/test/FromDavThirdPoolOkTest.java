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

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @version
 */
public class FromDavThirdPoolOkTest extends AbstractDavTest {

	private static int counter;
	private final String body = "Hello World this file will be deleted";

	private String getDavUrl() {
		return DAV_URL + "/thirdpool?delete=true";
	}

	@Override
	@Before
	public void setUp() throws Exception {
		deleteDirectory("tmpOut/thridpool");
		super.setUp();
	}

	@Test
	public void testPollFileAndShouldBeDeletedAtThirdPoll() throws Exception {
		template.sendBodyAndHeader(getDavUrl(), body, Exchange.FILE_NAME, "hello.txt");

		getMockEndpoint("mock:result").expectedBodiesReceived(body);
		// 2 first attempt should fail
		getMockEndpoint("mock:error").expectedMessageCount(2);

		assertMockEndpointsSatisfied();

		// give time to delete file
		Thread.sleep(200);

		assertEquals(3, counter);

		// assert the file is deleted
		File file = new File(DAV_ROOT_DIR + "/thirdpool/hello.txt");
		assertFalse("The file should have been deleted", file.exists());
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// no redeliveries as we want the dav consumer to try again
				errorHandler(deadLetterChannel("mock:error").maximumRedeliveries(0).logStackTrace(false));
				onException(IllegalArgumentException.class).handled(false); // DLC
																			// should
																			// not
																			// handle

				from(getDavUrl()).process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						counter++;
						if (counter < 3) {
							// file should exists
							File file = new File(DAV_ROOT_DIR + "/thirdpool/hello.txt");
							assertTrue("The file should NOT have been deleted", file.exists());
							throw new IllegalArgumentException("Forced by unittest");
						}
					}
				}).to("mock:result");
			}
		};
	}
}