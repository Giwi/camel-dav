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
public class DavRecursiveDepth3Test extends AbstractDavTest {

	protected String getDavUrl() {
		return DAV_URL + "/depth3?recursive=true";
	}

	@Test
	public void testDepthMin1Max1() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedBodiesReceivedInAnyOrder("a", "b");

		template.sendBodyAndHeader(DAV_URL + "/depth3?password=admin", "a", Exchange.FILE_NAME, "a.txt");
		template.sendBodyAndHeader(DAV_URL + "/depth3?password=admin", "b", Exchange.FILE_NAME, "b.txt");
		template.sendBodyAndHeader(DAV_URL + "/depth3/foo?password=admin", "a2", Exchange.FILE_NAME, "a.txt");
		template.sendBodyAndHeader(DAV_URL + "/depth3/foo/bar?password=admin", "a3", Exchange.FILE_NAME, "a.txt");
		template.sendBodyAndHeader(DAV_URL + "/depth3/bar?password=admin", "b2", Exchange.FILE_NAME, "b.txt");
		template.sendBodyAndHeader(DAV_URL + "/depth3/bar/foo?password=admin", "b3", Exchange.FILE_NAME, "b.txt");

		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getDavUrl() + "&minDepth=1&maxDepth=1").convertBodyTo(String.class).to("mock:result");
			}
		};
	}
}
