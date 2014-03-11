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

import java.io.File;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.junit.Test;

/**
 * Unit test for login failure due bad password and no re connect attempts allowed.
 */
public class DavThrowExceptionOnConnectionFailedTest extends AbstractDavTest {

	/**
	 * Test bad login.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testBadLogin() throws Exception {
		try {
			uploadFile("dummy", "cantremeber");
			fail("Should have thrown a GenericFileOperationFailedException");
		} catch (GenericFileOperationFailedException e) {
			// expected
			// assertEquals(530, e.getCode());
			// Hey WTF, Sardine return a 401 !!
			assertEquals(401, e.getCode());
		}

		// assert file NOT created
		File file = new File(DAV_ROOT_DIR + "login/report.txt");
		assertFalse("The file should NOT exists", file.exists());
	}

	/**
	 * Upload file.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws Exception
	 *             the exception
	 */
	private void uploadFile(String username, String password) throws Exception {
		Endpoint endpoint = context.getEndpoint("dav://" + username + "@localhost:80/webdavs2/login?password=" + password + "&maximumReconnectAttempts=0&throwExceptionOnConnectFailed=true");

		Exchange exchange = endpoint.createExchange();
		exchange.getIn().setBody("Hello World from FTPServer");
		exchange.getIn().setHeader(Exchange.FILE_NAME, "report.txt");
		Producer producer = endpoint.createProducer();
		producer.start();
		producer.process(exchange);
		producer.stop();
	}

}