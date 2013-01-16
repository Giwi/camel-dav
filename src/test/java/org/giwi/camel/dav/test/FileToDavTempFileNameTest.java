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
import org.junit.Test;

/**
 *
 */
public class FileToDavTempFileNameTest extends AbstractDavTest {

	@Test
	public void testFileToFtp() throws Exception {
		NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

		template.sendBodyAndHeader("file:src/main/data", "Hello World", Exchange.FILE_NAME, "sub/hello.txt");

		assertTrue(notify.matchesMockWaitTime());
		Thread.sleep(1000);
		File file = new File(DAV_ROOT_DIR + "/sub/hello.txt");
		assertTrue("File should exists " + file, file.exists());
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:src/main/data?recursive=true").to(DAV_URL + "?fileName=${file:name}&tempFileName=${file:onlyname}.part&stepwise=false");
			}
		};
	}
}
