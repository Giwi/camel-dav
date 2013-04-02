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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for DAV using expression (file language).
 */
public class DavConsumerMoveExpressionTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/filelanguage?consumer.delay=5000";
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
	deleteDirectory("tmpOut/filelanguage");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRegistry()
     */
    @Override
    protected JndiRegistry createRegistry() throws Exception {
	JndiRegistry jndi = super.createRegistry();
	jndi.bind("myguidgenerator", new MyGuidGenerator());
	return jndi;
    }

    /**
     * Test move using expression.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testMoveUsingExpression() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedBodiesReceived("Reports");

	sendFile(getDavUrl(), "Reports", "report2.txt");

	assertMockEndpointsSatisfied();

	// give time for consumer to rename file
	Thread.sleep(1000);

	String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
	File file = new File(DAV_ROOT_DIR + "/filelanguage/backup/" + now
		+ "/123-report2.bak");
	assertTrue("File should have been renamed", file.exists());
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
		from(
			getDavUrl()
				+ "&move=backup/${date:now:yyyyMMdd}/${bean:myguidgenerator}"
				+ "-${file:name.noext}.bak").to("mock:result");
	    }
	};
    }

    /**
     * The Class MyGuidGenerator.
     */
    public class MyGuidGenerator {

	/**
	 * Guid.
	 * 
	 * @return the string
	 */
	public String guid() {
	    return "123";
	}
    }
}