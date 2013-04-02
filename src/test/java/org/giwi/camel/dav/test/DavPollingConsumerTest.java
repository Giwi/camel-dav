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

import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.junit.Test;

/**
 * The Class DavPollingConsumerTest.
 */
public class DavPollingConsumerTest extends AbstractDavTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#isUseRouteBuilder()
     */
    @Override
    public boolean isUseRouteBuilder() {
	return false;
    }

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/polling";
    }

    /**
     * Test polling consumer.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testPollingConsumer() throws Exception {
	template.sendBodyAndHeader(getDavUrl(), "Hello World",
		Exchange.FILE_NAME, "hello.txt");

	PollingConsumer consumer = context.getEndpoint(getDavUrl())
		.createPollingConsumer();
	consumer.start();
	Exchange exchange = consumer.receive(5000);
	assertNotNull(exchange);
	assertEquals("Hello World", exchange.getIn().getBody(String.class));

	// sleep a bit to ensure polling consumer would be suspended after we
	// have used it
	Thread.sleep(1000);

	// drop a new file which should not be picked up by the consumer
	template.sendBodyAndHeader(getDavUrl(), "Bye World",
		Exchange.FILE_NAME, "bye.txt");

	// sleep a bit to ensure polling consumer would not have picked up that
	// file
	Thread.sleep(1000);

	File file = new File(DAV_ROOT_DIR + "/polling/bye.txt");
	assertTrue("File should exist " + file, file.exists());

	consumer.stop();
    }

}
