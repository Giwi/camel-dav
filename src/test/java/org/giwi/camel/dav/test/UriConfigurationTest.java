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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.giwi.camel.dav.DavConfiguration;
import org.giwi.camel.dav.DavEndpoint;
import org.giwi.camel.dav.RemoteFileConfiguration;
import org.giwi.camel.dav.RemoteFileEndpoint;
import org.junit.Test;

/**
 * The Class UriConfigurationTest.
 * 
 * @version
 */
public class UriConfigurationTest extends CamelTestSupport {

    /**
     * Test dav configuration defaults.
     */
    @Test
    public void testDavConfigurationDefaults() {
	DavEndpoint<?> endpoint = context.getEndpoint("dav://hostname",
		DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals(80, config.getPort());
	assertNull(config.getUsername());
	assertNull(config.getPassword());
	assertEquals(RemoteFileConfiguration.PathSeparator.Auto,
		config.getSeparator());
    }

    /**
     * Test dav explicit configuration.
     */
    @Test
    public void testDavExplicitConfiguration() {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://user@hostname:1021/some/file?password=secret",
		DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals(1021, config.getPort());
	assertEquals("user", config.getUsername());
	assertEquals("secret", config.getPassword());
    }

    /**
     * Test remote file endpoint files.
     */
    @Test
    public void testRemoteFileEndpointFiles() {
	assertRemoteFileEndpointFile("dav://hostname/foo/bar", "foo/bar");
	assertRemoteFileEndpointFile("dav://hostname/foo/bar/", "foo/bar/");
	assertRemoteFileEndpointFile("dav://hostname/foo/", "foo/");
	assertRemoteFileEndpointFile("dav://hostname/foo", "foo");
	assertRemoteFileEndpointFile("dav://hostname/", "");
	assertRemoteFileEndpointFile("dav://hostname", "");
	assertRemoteFileEndpointFile("dav://hostname//", "/");
	assertRemoteFileEndpointFile("dav://hostname//foo/bar", "/foo/bar");
	assertRemoteFileEndpointFile("dav://hostname//foo/bar/", "/foo/bar/");
	assertRemoteFileEndpointFile(
		"dav://user@hostname:123//foo/bar?password=secret", "/foo/bar");
	assertRemoteFileEndpointFile("dav://user@hostname:123?password=secret",
		"");
	assertRemoteFileEndpointFile("dav://hostname/foo/bar", "foo/bar");
	assertRemoteFileEndpointFile("dav://hostname/foo/bar/", "foo/bar/");
	assertRemoteFileEndpointFile("dav://hostname/foo/", "foo/");
	assertRemoteFileEndpointFile("dav://hostname/foo", "foo");
	assertRemoteFileEndpointFile("dav://hostname/", "");
	assertRemoteFileEndpointFile("dav://hostname", "");
	assertRemoteFileEndpointFile("dav://hostname//", "/");
	assertRemoteFileEndpointFile("dav://hostname//foo/bar", "/foo/bar");
	assertRemoteFileEndpointFile("dav://hostname//foo/bar/", "/foo/bar/");
	assertRemoteFileEndpointFile(
		"davs://user@hostname:123//foo/bar?password=secret", "/foo/bar");
	assertRemoteFileEndpointFile(
		"davs://user@hostname:123?password=secret", "");
	assertRemoteFileEndpointFile("davs://hostname/foo/bar", "foo/bar");
	assertRemoteFileEndpointFile("davs://hostname/foo/bar/", "foo/bar/");
	assertRemoteFileEndpointFile("davs://hostname/foo/", "foo/");
	assertRemoteFileEndpointFile("davs://hostname/foo", "foo");
	assertRemoteFileEndpointFile("davs://hostname/", "");
	assertRemoteFileEndpointFile("davs://hostname", "");
	assertRemoteFileEndpointFile("davs://hostname//", "/");
	assertRemoteFileEndpointFile("davs://hostname//foo/bar", "/foo/bar");
	assertRemoteFileEndpointFile("davs://hostname//foo/bar/", "/foo/bar/");
    }

    /**
     * Assert remote file endpoint file.
     * 
     * @param endpointUri
     *            the endpoint uri
     * @param expectedFile
     *            the expected file
     */
    private void assertRemoteFileEndpointFile(String endpointUri,
	    String expectedFile) {
	RemoteFileEndpoint<?> endpoint = resolveMandatoryEndpoint(context,
		endpointUri, RemoteFileEndpoint.class);
	assertNotNull("Could not find endpoint: " + endpointUri, endpoint);

	String file = endpoint.getConfiguration().getDirectory();
	assertEquals("For uri: " + endpointUri + " the file is not equal",
		expectedFile, file);
    }

    /**
     * Test password in context path configuration.
     */
    @Test
    public void testPasswordInContextPathConfiguration() {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://user:secret@hostname:1021/some/file", DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals(1021, config.getPort());
	assertEquals("user", config.getUsername());
	assertEquals("secret", config.getPassword());
    }

    /**
     * Test starting directory with dot.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testStartingDirectoryWithDot() throws Exception {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://user@hostname?password=secret", DavEndpoint.class);
	DavConfiguration config = endpoint.getConfiguration();
	config.setHost("somewhere");
	config.setDirectory("temp.dir");
	endpoint.createConsumer(new Processor() {
	    @Override
	    public void process(Exchange exchange) throws Exception {
		// do nothing
	    }
	});
    }

    /**
     * Test path separator auto.
     */
    @Test
    public void testPathSeparatorAuto() {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://hostname/foo/bar?separator=Auto", DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals("foo/bar", config.getDirectory());
	assertEquals(RemoteFileConfiguration.PathSeparator.Auto,
		config.getSeparator());

	assertEquals("foo/bar/hello.txt",
		config.normalizePath("foo/bar/hello.txt"));
	assertEquals("foo\\bar\\hello.txt",
		config.normalizePath("foo\\bar\\hello.txt"));
    }

    /**
     * Test path separator unix.
     */
    @Test
    public void testPathSeparatorUnix() {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://hostname/foo/bar?separator=UNIX", DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals("foo/bar", config.getDirectory());
	assertEquals(RemoteFileConfiguration.PathSeparator.UNIX,
		config.getSeparator());

	assertEquals("foo/bar/hello.txt",
		config.normalizePath("foo/bar/hello.txt"));
	assertEquals("foo/bar/hello.txt",
		config.normalizePath("foo\\bar\\hello.txt"));
    }

    /**
     * Test path separator windows.
     */
    @Test
    public void testPathSeparatorWindows() {
	DavEndpoint<?> endpoint = context.getEndpoint(
		"dav://hostname/foo/bar?separator=Windows", DavEndpoint.class);
	RemoteFileConfiguration config = endpoint.getConfiguration();

	assertEquals("http", config.getProtocol());
	assertEquals("hostname", config.getHost());
	assertEquals("foo/bar", config.getDirectory());
	assertEquals(RemoteFileConfiguration.PathSeparator.Windows,
		config.getSeparator());

	assertEquals("foo\\bar\\hello.txt",
		config.normalizePath("foo/bar/hello.txt"));
	assertEquals("foo\\bar\\hello.txt",
		config.normalizePath("foo\\bar\\hello.txt"));
    }

}