/**
 * 
 */
package org.giwi.camel.dav;

import com.googlecode.sardine.DavResource;

/**
 * @author xavier
 * 
 */
public class DavProducer extends RemoteFileProducer<DavResource> {
	protected String endpointPath;

	protected DavProducer(RemoteFileEndpoint<DavResource> endpoint, RemoteFileOperations<DavResource> operations) {
		super(endpoint, operations);
		endpointPath = endpoint.getConfiguration().getRemoteServerInformation();
		if (log.isInfoEnabled()) {
			log.info("endpointPath : " + endpointPath);
		}
		((DavOperations) operations).initComponent(endpoint.getConfiguration().getInitialDirectory());
	}

}
