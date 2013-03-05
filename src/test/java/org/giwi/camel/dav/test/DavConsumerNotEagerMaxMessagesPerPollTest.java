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
import org.junit.Before;
import org.junit.Test;

/**
 * @version
 */
public class DavConsumerNotEagerMaxMessagesPerPollTest extends AbstractDavTest {

	private String getDavUrl() {
		return DAV_URL + "/poll/?delay=6000&delete=true&sortBy=file:name&maxMessagesPerPoll=2&eagerMaxMessagesPerPoll=false";
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		prepareDavServer();
	}

	@Test
	public void testMaxMessagesPerPoll() throws Exception {
		// start route
		context.startRoute("foo");

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedBodiesReceived("AAA", "BBB");
		mock.setResultWaitTime(4000);
		mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 2);

		assertMockEndpointsSatisfied();

		mock.reset();
		mock.expectedBodiesReceived("CCC");
		mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 1);

		assertMockEndpointsSatisfied();
	}

	private void prepareDavServer() throws Exception {
		sendFile(getDavUrl(), "CCC", "ccc.txt");
		sendFile(getDavUrl(), "AAA", "aaa.txt");
		sendFile(getDavUrl(), "BBB", "bbb.txt");
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getDavUrl()).noAutoStartup().routeId("foo").to("mock:result");
			}
		};
	}
}