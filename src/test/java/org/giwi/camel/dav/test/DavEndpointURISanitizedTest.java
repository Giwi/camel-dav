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

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Producer;
import org.junit.Test;

/**
 * Test to ensure the DavEndpoint URI is sanitized.
 */
public class DavEndpointURISanitizedTest extends AbstractDavTest {

    /** The password. */
    private final String password = "secret";

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "///foo?delay=5000";
    }

    /**
     * Test dav consumer uri sanitized.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDavConsumerUriSanitized() throws Exception {
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Consumer consumer = endpoint.createConsumer(null);
	assertFalse(consumer.toString().contains(password));
    }

    /**
     * Test dav producer uri sanitized.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDavProducerUriSanitized() throws Exception {
	Endpoint endpoint = context.getEndpoint(getDavUrl());
	Producer producer = endpoint.createProducer();
	assertFalse(producer.toString().contains(password));
    }
}
