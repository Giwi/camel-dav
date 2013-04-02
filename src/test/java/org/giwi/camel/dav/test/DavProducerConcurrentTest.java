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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

/**
 * Concurrent producers test.
 * 
 * @version
 */
public class DavProducerConcurrentTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/concurrent";
    }

    /**
     * Test no concurrent producers.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testNoConcurrentProducers() throws Exception {
	doSendMessages(1, 1);
    }

    /**
     * Test concurrent producers.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testConcurrentProducers() throws Exception {
	doSendMessages(10, 5);
    }

    /**
     * Do send messages.
     * 
     * @param files
     *            the files
     * @param poolSize
     *            the pool size
     * @throws Exception
     *             the exception
     */
    private void doSendMessages(int files, int poolSize) throws Exception {
	getMockEndpoint("mock:result").expectedMessageCount(files);

	ExecutorService executor = Executors.newFixedThreadPool(poolSize);
	for (int i = 0; i < files; i++) {
	    getMockEndpoint("mock:result").expectedFileExists(
		    DAV_ROOT_DIR + "/concurrent/" + i + ".txt");

	    final int index = i;
	    executor.submit(new Callable<Object>() {
		@Override
		public Object call() throws Exception {
		    sendFile("direct:start", "Hello World", index + ".txt");
		    return null;
		}
	    });
	}

	assertMockEndpointsSatisfied();
	executor.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from("direct:start").to(getDavUrl(), "mock:result");
	    }
	};
    }
}