/**
 * 
 */
package org.giwi.camel.dav;

import java.net.URI;

/**
 * @author xavier
 * 
 */
public class DavConfiguration extends RemoteFileConfiguration {

	public DavConfiguration() {
		setProtocol("ftp");
	}

	public DavConfiguration(URI uri) {
		super(uri);
	}

	@Override
	protected void setDefaultPort() {
		setPort(80);
	}
}
