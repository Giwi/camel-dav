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

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavProducerRootFileExistFailTest.
 * 
 * @version
 */
public class DavProducerRootFileExistFailTest extends AbstractDavTest {

	/**
	 * Gets the dav url.
	 * 
	 * @return the dav url
	 */
	private String getDavUrl() {
		return DAV_URL + "?fileExist=Fail";
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
		// create existing file on dav server
		template.sendBodyAndHeader(getDavUrl(), "Hello World", Exchange.FILE_NAME, "hello.txt");
	}

	/**
	 * Test fail.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testFail() throws Exception {
		try {
			template.sendBodyAndHeader(getDavUrl(), "Bye World", Exchange.FILE_NAME, "hello.txt");
			fail("Should have thrown an exception");
		} catch (CamelExecutionException e) {
			GenericFileOperationFailedException cause = assertIsInstanceOf(GenericFileOperationFailedException.class, e.getCause());
			assertEquals("File already exist: webdav/hello.txt. Cannot write new file.", cause.getMessage());
		}

		// root file should still exist
		assertFileExists(DAV_ROOT_DIR + "/hello.txt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.test.junit4.CamelTestSupport#isUseRouteBuilder()
	 */
	@Override
	public boolean isUseRouteBuilder() {
		return false;
	}
}