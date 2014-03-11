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

import static org.apache.camel.language.simple.SimpleLanguage.simple;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * The Class AbstractDavTest.
 * 
 * @author xavier
 */
public abstract class AbstractDavTest extends CamelTestSupport {

	/** The Constant DAV_ROOT_DIR. */
	protected static final String DAV_ROOT_DIR = "/var/www/web1/web";

	/** The Constant DAV_URL. */
	protected static final String DAV_URL = "dav://localhost:80/webdav";
	{
		for (File f : new File(DAV_ROOT_DIR).listFiles()) {
			deleteDirectory(f);
		}

	}

	/**
	 * Send file.
	 * 
	 * @param url
	 *            the url
	 * @param body
	 *            the body
	 * @param fileName
	 *            the file name
	 */
	public void sendFile(String url, Object body, String fileName) {
		template.sendBodyAndHeader(url, body, Exchange.FILE_NAME, simple(fileName));
	}
}
