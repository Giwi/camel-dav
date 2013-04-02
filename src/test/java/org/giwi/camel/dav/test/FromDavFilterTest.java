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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;

/**
 * Unit test to test filter option.
 */
public class FromDavFilterTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/filter?filter=#myFilter";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRegistry()
     */
    @Override
    protected JndiRegistry createRegistry() throws Exception {
	JndiRegistry jndi = super.createRegistry();
	jndi.bind("myFilter", new MyFileFilter<Object>());
	return jndi;
    }

    /**
     * Test filter files.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testFilterFiles() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(0);

	sendFile(getDavUrl(), "This is a file to be filtered", "skipme.txt");

	mock.setResultWaitTime(3000);
	mock.assertIsSatisfied();
    }

    /**
     * Test filter files with a regular file.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testFilterFilesWithARegularFile() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(1);
	mock.expectedBodiesReceived("Hello World");

	sendFile(getDavUrl(), "This is a file to be filtered", "skipme.txt");

	sendFile(getDavUrl(), "Hello World", "hello.txt");

	mock.assertIsSatisfied();
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
		from(getDavUrl()).to("mock:result");
	    }
	};
    }

    // START SNIPPET: e1
    /**
     * The Class MyFileFilter.
     * 
     * @param <T>
     *            the generic type
     */
    public class MyFileFilter<T> implements GenericFileFilter<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.camel.component.file.GenericFileFilter#accept(org.apache
	 * .camel.component.file.GenericFile)
	 */
	@Override
	public boolean accept(GenericFile<T> file) {
	    // we don't accept any files starting with skip in the name
	    return !file.getFileName().startsWith("skip");
	}
    }
    // END SNIPPET: e1
}