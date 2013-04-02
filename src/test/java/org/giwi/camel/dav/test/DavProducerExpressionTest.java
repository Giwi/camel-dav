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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.camel.impl.JndiRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for DAV using expression (file language).
 */
public class DavProducerExpressionTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/filelanguage";
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
     * Test produce bean by expression.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProduceBeanByExpression() throws Exception {
	template.sendBody(
		getDavUrl() + "?fileName=${bean:myguidgenerator}.bak",
		"Hello World");

	assertFileExists(DAV_ROOT_DIR + "/filelanguage/123.bak");
    }

    /**
     * Test produce bean by header.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProduceBeanByHeader() throws Exception {
	sendFile(getDavUrl(), "Hello World", "${bean:myguidgenerator}.bak");

	assertFileExists(DAV_ROOT_DIR + "/filelanguage/123.bak");
    }

    /**
     * Test producer date by header.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerDateByHeader() throws Exception {
	sendFile(getDavUrl(), "Hello World", "myfile-${date:now:yyyyMMdd}.txt");

	String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
	assertFileExists(DAV_ROOT_DIR + "/filelanguage/myfile-" + date + ".txt");
    }

    /**
     * Test producer date by expression.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerDateByExpression() throws Exception {
	template.sendBody(getDavUrl()
		+ "?fileName=myfile-${date:now:yyyyMMdd}.txt", "Hello World");

	String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
	assertFileExists(DAV_ROOT_DIR + "/filelanguage/myfile-" + date + ".txt");
    }

    /**
     * Test producer complex by expression.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerComplexByExpression() throws Exception {
	// need one extra subdirectory (=foo) to be able to start with .. in the
	// fileName option
	String url = DAV_URL + "/filelanguage/foo?password=admin";

	String expression = "../filelanguageinbox/myfile-${bean:myguidgenerator.guid}-${date:now:yyyyMMdd}.txt";
	template.sendBody(url + "&fileName=" + expression, "Hello World");

	String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
	assertFileExists(DAV_ROOT_DIR
		+ "/filelanguage/filelanguageinbox/myfile-123-" + date + ".txt");
    }

    /**
     * Test producer simple with header by expression.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerSimpleWithHeaderByExpression() throws Exception {
	template.sendBodyAndHeader(getDavUrl()
		+ "?fileName=myfile-${in.header.foo}.txt", "Hello World",
		"foo", "abc");

	assertFileExists(DAV_ROOT_DIR + "/filelanguage/myfile-abc.txt");
    }

    /**
     * Test producer with date header.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerWithDateHeader() throws Exception {
	Calendar cal = Calendar.getInstance();
	cal.set(1974, Calendar.APRIL, 20);
	Date date = cal.getTime();

	template.sendBodyAndHeader(
		getDavUrl()
			+ "?fileName=mybirthday-${date:in.header.birthday:yyyyMMdd}.txt",
		"Hello World", "birthday", date);

	assertFileExists(DAV_ROOT_DIR + "/filelanguage/mybirthday-19740420.txt");
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