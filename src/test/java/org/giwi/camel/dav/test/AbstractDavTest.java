/**
 * 
 */
package org.giwi.camel.dav.test;

import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * @author xavier
 * 
 */
public abstract class AbstractDavTest extends CamelTestSupport {
	protected static final String DAV_ROOT_DIR = "/var/www/web1/web";
	protected static final String DAV_URL = "dav://localhost:80/webdav";
}
