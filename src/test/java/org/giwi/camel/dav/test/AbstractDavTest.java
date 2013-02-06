/**
 * 
 */
package org.giwi.camel.dav.test;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * @author xavier
 * 
 */
public abstract class AbstractDavTest extends CamelTestSupport {
	protected static final String DAV_ROOT_DIR = "/var/www/web1/web";
	protected static final String DAV_URL = "dav://localhost:80/webdav";
	{
		for (File f : new File(DAV_ROOT_DIR).listFiles()) {
			deleteDirectory(f);
		}

	}

	/**
	 * @param url
	 * @param body
	 * @param fileName
	 */
	public void sendFile(String url, Object body, String fileName) {
		template.sendBodyAndHeader(url, body, Exchange.FILE_NAME, fileName);
	}
}
