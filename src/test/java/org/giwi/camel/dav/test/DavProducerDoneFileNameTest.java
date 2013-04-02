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

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ExpressionIllegalSyntaxException;
import org.junit.Test;

/**
 * The Class DavProducerDoneFileNameTest.
 * 
 * @version
 */
public class DavProducerDoneFileNameTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/done";
    }

    /**
     * Test producer constant done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerConstantDoneFileName() throws Exception {
	template.sendBodyAndHeader(getDavUrl() + "?doneFileName=done",
		"Hello World", Exchange.FILE_NAME, "hello.txt");

	File file = new File(DAV_ROOT_DIR + "/done/hello.txt");
	assertEquals("File should exists", true, file.exists());

	File done = new File(DAV_ROOT_DIR + "/done/done");
	assertEquals("Done file should exists", true, done.exists());
    }

    /**
     * Test producer prefix done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerPrefixDoneFileName() throws Exception {
	template.sendBodyAndHeader(getDavUrl()
		+ "?doneFileName=done-${file:name}", "Hello World",
		Exchange.FILE_NAME, "hello.txt");

	File file = new File(DAV_ROOT_DIR + "/done/hello.txt");
	assertEquals("File should exists", true, file.exists());

	File done = new File(DAV_ROOT_DIR + "/done/done-hello.txt");
	assertEquals("Done file should exists", true, done.exists());
    }

    /**
     * Test producer ext done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerExtDoneFileName() throws Exception {
	template.sendBodyAndHeader(getDavUrl()
		+ "?doneFileName=${file:name}.done", "Hello World",
		Exchange.FILE_NAME, "hello.txt");

	File file = new File(DAV_ROOT_DIR + "/done/hello.txt");
	assertEquals("File should exists", true, file.exists());

	File done = new File(DAV_ROOT_DIR + "/done/hello.txt.done");
	assertEquals("Done file should exists", true, done.exists());
    }

    /**
     * Test producer replace ext done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerReplaceExtDoneFileName() throws Exception {
	template.sendBodyAndHeader(getDavUrl()
		+ "?doneFileName=${file:name.noext}.done", "Hello World",
		Exchange.FILE_NAME, "hello.txt");

	File file = new File(DAV_ROOT_DIR + "/done/hello.txt");
	assertEquals("File should exists", true, file.exists());

	File done = new File(DAV_ROOT_DIR + "/done/hello.done");
	assertEquals("Done file should exists", true, done.exists());
    }

    /**
     * Test producer invalid done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerInvalidDoneFileName() throws Exception {
	try {
	    template.sendBodyAndHeader(getDavUrl()
		    + "?doneFileName=${file:parent}/foo", "Hello World",
		    Exchange.FILE_NAME, "hello.txt");
	    fail("Should have thrown exception");
	} catch (CamelExecutionException e) {
	    ExpressionIllegalSyntaxException cause = assertIsInstanceOf(
		    ExpressionIllegalSyntaxException.class, e.getCause());
	    assertTrue(
		    cause.getMessage(),
		    cause.getMessage().endsWith(
			    "Cannot resolve reminder: ${file:parent}/foo"));
	}
    }

    /**
     * Test producer empty done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerEmptyDoneFileName() throws Exception {
	try {
	    template.sendBodyAndHeader(getDavUrl() + "?doneFileName=",
		    "Hello World", Exchange.FILE_NAME, "hello.txt");
	    fail("Should have thrown exception");
	} catch (CamelExecutionException e) {
	    IllegalArgumentException cause = assertIsInstanceOf(
		    IllegalArgumentException.class, e.getCause());
	    assertTrue(
		    cause.getMessage(),
		    cause.getMessage().startsWith(
			    "doneFileName must be specified and not empty"));
	}
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
