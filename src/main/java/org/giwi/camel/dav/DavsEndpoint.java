package org.giwi.camel.dav;

import org.apache.camel.component.file.GenericFileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.DavResource;

public class DavsEndpoint<T extends DavResource> extends
	DavEndpoint<DavResource> {
    /** The Constant LOG. */
    private static final transient Logger LOG = LoggerFactory
	    .getLogger(DavsEndpoint.class);

    /**
     * Instantiates a new davs endpoint.
     */
    public DavsEndpoint() {
    }

    /**
     * Instantiates a new davs
     * 
     * @param uri
     *            the uri
     * @param component
     *            the component
     * @param configuration
     *            the configuration
     */
    public DavsEndpoint(String uri, RemoteFileComponent<DavResource> component,
	    RemoteFileConfiguration configuration) {
	super(uri, component, configuration);
	if (LOG.isInfoEnabled()) {
	    LOG.info(uri);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.giwi.camel.dav.RemoteFileEndpoint#getConfiguration()
     */
    @Override
    public DavsConfiguration getConfiguration() {
	if (configuration == null) {
	    configuration = new DavsConfiguration();
	}
	return (DavsConfiguration) configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#setConfiguration(
     * org.apache.camel.component.file.GenericFileConfiguration)
     */
    @Override
    public void setConfiguration(GenericFileConfiguration configuration) {
	setConfiguration(configuration);
    }

    /**
     * Sets the configuration.
     * 
     * @param configuration
     *            the new configuration
     */
    @Override
    public void setConfiguration(RemoteFileConfiguration configuration) {
	if (configuration == null) {
	    throw new IllegalArgumentException("DavsConfiguration expected");
	}
	this.configuration = configuration;
    }
}
