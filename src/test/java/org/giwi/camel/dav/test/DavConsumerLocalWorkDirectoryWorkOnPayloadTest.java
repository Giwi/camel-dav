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
import org.apache.camel.builder.RouteBuilder;

/**
 * The Class DavConsumerLocalWorkDirectoryWorkOnPayloadTest.
 * 
 * @version
 */
public class DavConsumerLocalWorkDirectoryWorkOnPayloadTest extends
	DavConsumerLocalWorkDirectoryTest {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.giwi.camel.dav.test.DavConsumerLocalWorkDirectoryTest#createRouteBuilder
     * ()
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(getDavUrl()).process(new Processor() {
		    @Override
		    public void process(Exchange exchange) throws Exception {
			// alter the body by setting it to a String type
			exchange.getIn().setBody("Hello World");

		    }
		}).to("mock:result", "file://tmpOut/out");
	    }
	};
    }

}