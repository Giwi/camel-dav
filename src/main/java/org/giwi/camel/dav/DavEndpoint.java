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
package org.giwi.camel.dav;

import org.apache.camel.FailedToCreateConsumerException;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.component.file.GenericFileProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

/**
 * Represents a DAV endpoint.
 * 
 * @param <T>
 *            the DavResource
 * @author Giwi Softwares
 */
public class DavEndpoint<T extends DavResource> extends
	RemoteFileEndpoint<DavResource> {

    /** The dav client. */
    protected Sardine davClient;

    /** The Constant LOG. */
    private static final transient Logger LOG = LoggerFactory
	    .getLogger(DavEndpoint.class);

    /**
     * Instantiates a new dav endpoint.
     */
    public DavEndpoint() {
    }

    /**
     * Instantiates a new dav endpoint.
     * 
     * @param uri
     *            the uri
     * @param component
     *            the component
     * @param configuration
     *            the configuration
     */
    public DavEndpoint(String uri, RemoteFileComponent<DavResource> component,
	    RemoteFileConfiguration configuration) {
	super(uri, component, configuration);
	if (LOG.isInfoEnabled()) {
	    LOG.info(uri);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileEndpoint#getScheme()
     */
    @Override
    public String getScheme() {
	return "dav";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.giwi.camel.dav.RemoteFileEndpoint#buildConsumer(org.apache.camel.
     * Processor)
     */
    @Override
    protected RemoteFileConsumer<DavResource> buildConsumer(Processor processor) {
	try {
	    return new DavConsumer(this, processor,
		    createRemoteFileOperations());
	} catch (GenericFileOperationFailedException e) {
	    throw e;
	} catch (Exception e) {
	    throw new FailedToCreateConsumerException(this, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.giwi.camel.dav.RemoteFileEndpoint#buildProducer()
     */
    @Override
    protected GenericFileProducer<DavResource> buildProducer() {
	try {
	    return new DavProducer(this, createRemoteFileOperations());
	} catch (GenericFileOperationFailedException e) {
	    throw e;
	} catch (Exception e) {
	    throw new FailedToCreateProducerException(this, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.giwi.camel.dav.RemoteFileEndpoint#createRemoteFileOperations()
     */
    @Override
    public RemoteFileOperations<DavResource> createRemoteFileOperations()
	    throws Exception {
	// configure dav client
	Sardine client = davClient;

	if (client == null) {
	    // must use a new client if not explicit configured to use a custom
	    // client

	    if (!"".equals(((DavConfiguration) configuration).getUsername())) {
		client = SardineFactory.begin(
			((DavConfiguration) configuration).getUsername(),
			((DavConfiguration) configuration).getPassword());
	    } else {
		client = SardineFactory.begin();
		client.enablePreemptiveAuthentication(((DavConfiguration) configuration)
			.getHost());

	    }
	}

	DavOperations operations = new DavOperations(client);
	operations.setEndpoint(this);
	return operations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.giwi.camel.dav.RemoteFileEndpoint#getConfiguration()
     */
    @Override
    public DavConfiguration getConfiguration() {
	if (configuration == null) {
	    configuration = new DavConfiguration();
	}
	return (DavConfiguration) configuration;
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
	setConfiguration((DavConfiguration) configuration);
    }

    /**
     * Sets the configuration.
     * 
     * @param configuration
     *            the new configuration
     */
    public void setConfiguration(RemoteFileConfiguration configuration) {
	if (configuration == null) {
	    throw new IllegalArgumentException("DavConfiguration expected");
	}
	this.configuration = configuration;
    }

    /**
     * Gets the dav client.
     * 
     * @return the davClient
     */
    public Sardine getDavClient() {
	return davClient;
    }

    /**
     * Sets the dav client.
     * 
     * @param davClient
     *            the davClient to set
     */
    public void setDavClient(Sardine davClient) {
	this.davClient = davClient;
    }

}
