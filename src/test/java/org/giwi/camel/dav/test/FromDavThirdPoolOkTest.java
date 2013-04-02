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
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class FromDavThirdPoolOkTest.
 * 
 * @version
 */
public class FromDavThirdPoolOkTest extends AbstractDavTest {

    /** The counter. */
    private static int counter;

    /** The body. */
    private final String body = "Hello World this file will be deleted";

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/thirdpool?delete=true";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
	deleteDirectory("tmpOut/thridpool");
	super.setUp();
    }

    /**
     * Test poll file and should be deleted at third poll.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testPollFileAndShouldBeDeletedAtThirdPoll() throws Exception {
	template.sendBodyAndHeader(getDavUrl(), body, Exchange.FILE_NAME,
		"hello.txt");

	getMockEndpoint("mock:result").expectedBodiesReceived(body);
	// 2 first attempt should fail
	getMockEndpoint("mock:error").expectedMessageCount(2);

	assertMockEndpointsSatisfied();

	// give time to delete file
	Thread.sleep(200);

	assertEquals(3, counter);

	// assert the file is deleted
	File file = new File(DAV_ROOT_DIR + "/thirdpool/hello.txt");
	assertFalse("The file should have been deleted", file.exists());
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
		// no redeliveries as we want the dav consumer to try again
		errorHandler(deadLetterChannel("mock:error")
			.maximumRedeliveries(0).logStackTrace(false));
		onException(IllegalArgumentException.class).handled(false); // DLC
									    // should
									    // not
									    // handle

		from(getDavUrl()).process(new Processor() {
		    @Override
		    public void process(Exchange exchange) throws Exception {
			counter++;
			if (counter < 3) {
			    // file should exists
			    File file = new File(DAV_ROOT_DIR
				    + "/thirdpool/hello.txt");
			    assertTrue("The file should NOT have been deleted",
				    file.exists());
			    throw new IllegalArgumentException(
				    "Forced by unittest");
			}
		    }
		}).to("mock:result");
	    }
	};
    }
}