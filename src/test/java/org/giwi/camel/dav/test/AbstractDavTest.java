/**
 * 
 */
package org.giwi.camel.dav.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		try {
			for (File f : new File(DAV_ROOT_DIR).listFiles()) {
				delete(f);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void delete(File f) throws IOException {
		System.out.println(f.getAbsolutePath());
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + f);
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
