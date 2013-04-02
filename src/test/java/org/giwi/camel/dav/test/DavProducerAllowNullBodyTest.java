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
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.junit.Test;

/**
 * The Class DavProducerAllowNullBodyTest.
 */
public class DavProducerAllowNullBodyTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/allownull?fileName=allowNullBody.txt";
    }

    /**
     * Test allow null body true.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAllowNullBodyTrue() throws Exception {
	template.sendBody(getDavUrl() + "&allowNullBody=true", null);

	assertFileExists(DAV_ROOT_DIR + "/allownull/allowNullBody.txt");
    }

    /**
     * Test allow null body false.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAllowNullBodyFalse() throws Exception {
	try {
	    template.sendBody(getDavUrl() + "&allowNullBody=false", null);
	    fail("Should have thrown a GenericFileOperationFailedException");
	} catch (CamelExecutionException e) {
	    GenericFileOperationFailedException cause = assertIsInstanceOf(
		    GenericFileOperationFailedException.class, e.getCause());
	    assertTrue(cause.getMessage().endsWith("allowNullBody.txt"));
	}

	assertFalse(
		"allowNullBody set to false with null body should not create a new file",
		new File(DAV_ROOT_DIR + "/allownull/allowNullBody.txt")
			.exists());
    }

}