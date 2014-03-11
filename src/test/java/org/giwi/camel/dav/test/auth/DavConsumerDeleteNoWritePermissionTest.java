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
package org.giwi.camel.dav.test.auth;

import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.giwi.camel.dav.test.AbstractDavTest;
import org.junit.Before;
import org.junit.Test;

/**
 * User does not have write permissions so can't deleted consumed file.
 */
public class DavConsumerDeleteNoWritePermissionTest extends AbstractDavTest {

	/**
	 * Gets the dav url.
	 * 
	 * @return the dav url
	 */
	private String getDavUrl() {
		return "dav://dummy@localhost:80/webdavs2/deletenoperm?password=foo&autoCreate=false&delete=true&consumer.delay=5000";
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
	 * Test consumer delete no write permission.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testConsumerDeleteNoWritePermission() throws Exception {
		PollingConsumer consumer = context.getEndpoint(getDavUrl()).createPollingConsumer();
		consumer.start();
		Exchange out = consumer.receive(3000);
		assertNotNull("Should get the file", out);

		try {
			// give consumer time to try to delete the file
			Thread.sleep(1000);
			consumer.stop();
		} catch (GenericFileOperationFailedException fofe) {
			// expected, ignore
		}
	}

	/**
	 * Prepare dav server.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void prepareDavServer() throws Exception {
		// prepares the DAV Server by creating files on the server that we want
		// to unit
		// test that we can pool and store as a local file
		String davUrl = DAV_URL + "/deletenoperm/";
		template.sendBodyAndHeader(davUrl, "Hello World", Exchange.FILE_NAME, "hello.txt");
	}
}