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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * Unit test to verify polling a server with no files to poll.
 */
public class FromDavNoFilesTest extends AbstractDavTest {

	private String getDavUrl() {
		return DAV_URL + "/slowfile?readLock=rename&consumer.delay=2000";
	}

	@Test
	public void testPoolIn3SecondsButNoFiles() throws Exception {
		deleteDirectory(DAV_ROOT_DIR + "slowfile");
		createDirectory(DAV_ROOT_DIR + "slowfile");
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(0);

		Thread.sleep(3 * 1000L);

		mock.assertIsSatisfied();
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