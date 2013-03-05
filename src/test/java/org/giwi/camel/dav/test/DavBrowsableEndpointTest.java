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
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.BrowsableEndpoint;
import org.giwi.camel.dav.DavEndpoint;
import org.junit.Test;

/**
 * @version
 */
public class DavBrowsableEndpointTest extends AbstractDavTest {

	private String getDavUrl() {
		return DAV_URL + "/browse";
	}

	@Override
	public boolean isUseRouteBuilder() {
		return false;
	}

	@Test
	public void testBrowsableNoFiles() throws Exception {
		// make sure starting directory exists
		createDirectory(DAV_ROOT_DIR + "/browse");

		BrowsableEndpoint browse = context.getEndpoint(getDavUrl(), BrowsableEndpoint.class);
		assertNotNull(browse);

		List<Exchange> list = browse.getExchanges();
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testBrowsableOneFile() throws Exception {
		template.sendBodyAndHeader(getDavUrl(), "A", Exchange.FILE_NAME, "a.txt");

		DavEndpoint<?> endpoint = context.getEndpoint(getDavUrl(), DavEndpoint.class);
		assertNotNull(endpoint);

		MemoryIdempotentRepository repo = (MemoryIdempotentRepository) endpoint.getInProgressRepository();
		assertEquals(0, repo.getCacheSize());

		List<Exchange> list = endpoint.getExchanges();
		assertNotNull(list);
		assertEquals(1, list.size());

		assertEquals("a.txt", list.get(0).getIn().getHeader(Exchange.FILE_NAME));

		// the in progress repo should not leak
		assertEquals(0, repo.getCacheSize());

		// and the file is still there
		File file = new File(DAV_ROOT_DIR + "/browse/a.txt");
		assertTrue("File should exist " + file, file.exists());
	}

	@Test
	public void testBrowsableTwoFiles() throws Exception {
		template.sendBodyAndHeader(getDavUrl(), "A", Exchange.FILE_NAME, "a.txt");
		template.sendBodyAndHeader(getDavUrl(), "B", Exchange.FILE_NAME, "b.txt");

		DavEndpoint<?> endpoint = context.getEndpoint(getDavUrl() + "?sortBy=file:name", DavEndpoint.class);
		assertNotNull(endpoint);

		MemoryIdempotentRepository repo = (MemoryIdempotentRepository) endpoint.getInProgressRepository();
		assertEquals(0, repo.getCacheSize());

		List<Exchange> list = endpoint.getExchanges();
		assertNotNull(list);
		assertEquals(2, list.size());

		assertEquals("a.txt", list.get(0).getIn().getHeader(Exchange.FILE_NAME));
		assertEquals("b.txt", list.get(1).getIn().getHeader(Exchange.FILE_NAME));

		// the in progress repo should not leak
		assertEquals(0, repo.getCacheSize());

		// and the files is still there
		File fileA = new File(DAV_ROOT_DIR + "/browse/a.txt");
		assertTrue("File should exist " + fileA, fileA.exists());
		File fileB = new File(DAV_ROOT_DIR + "/browse/b.txt");
		assertTrue("File should exist " + fileB, fileB.exists());
	}

	@Test
	public void testBrowsableThreeFilesRecursive() throws Exception {
		template.sendBodyAndHeader(getDavUrl(), "A", Exchange.FILE_NAME, "a.txt");
		template.sendBodyAndHeader(getDavUrl(), "B", Exchange.FILE_NAME, "foo/b.txt");
		template.sendBodyAndHeader(getDavUrl(), "C", Exchange.FILE_NAME, "bar/c.txt");

		DavEndpoint<?> endpoint = context.getEndpoint(getDavUrl() + "?recursive=true&sortBy=file:name", DavEndpoint.class);
		assertNotNull(endpoint);

		MemoryIdempotentRepository repo = (MemoryIdempotentRepository) endpoint.getInProgressRepository();
		assertEquals(0, repo.getCacheSize());

		List<Exchange> list = endpoint.getExchanges();
		assertNotNull(list);
		assertEquals(3, list.size());

		assertEquals("a.txt", list.get(0).getIn().getHeader(Exchange.FILE_NAME));
		assertEquals("c.txt", list.get(1).getIn().getHeader(Exchange.FILE_NAME_ONLY));
		assertEquals("b.txt", list.get(2).getIn().getHeader(Exchange.FILE_NAME_ONLY));

		// the in progress repo should not leak
		assertEquals(0, repo.getCacheSize());

		// and the files is still there
		File fileA = new File(DAV_ROOT_DIR + "/browse/a.txt");
		assertTrue("File should exist " + fileA, fileA.exists());
		File fileB = new File(DAV_ROOT_DIR + "/browse/foo/b.txt");
		assertTrue("File should exist " + fileB, fileB.exists());
		File fileC = new File(DAV_ROOT_DIR + "/browse/bar/c.txt");
		assertTrue("File should exist " + fileC, fileC.exists());
	}
}