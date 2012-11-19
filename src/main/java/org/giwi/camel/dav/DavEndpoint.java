package org.giwi.camel.dav;

import org.apache.camel.FailedToCreateConsumerException;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.component.file.GenericFileProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

/**
 * Represents a Sardine endpoint.
 */
public class DavEndpoint<T extends DavResource> extends RemoteFileEndpoint<DavResource> {
	protected Sardine davClient;
	private static final transient Logger LOG = LoggerFactory.getLogger(DavEndpoint.class);

	public DavEndpoint() {
	}

	public DavEndpoint(String uri, RemoteFileComponent<DavResource> component, RemoteFileConfiguration configuration) {
		super(uri, component, configuration);
		LOG.info(uri);
	}

	@Override
	public String getScheme() {
		return "dav";
	}

	@Override
	protected RemoteFileConsumer<DavResource> buildConsumer(Processor processor) {
		try {
			return new DavConsumer(this, processor, createRemoteFileOperations());
		} catch (Exception e) {
			throw new FailedToCreateConsumerException(this, e);
		}
	}

	@Override
	protected GenericFileProducer<DavResource> buildProducer() {
		try {
			return new RemoteFileProducer<DavResource>(this, createRemoteFileOperations());
		} catch (Exception e) {
			throw new FailedToCreateProducerException(this, e);
		}
	}

	@Override
	public RemoteFileOperations<DavResource> createRemoteFileOperations() throws Exception {
		// configure dav client
		Sardine client = davClient;

		if (client == null) {
			// must use a new client if not explicit configured to use a custom client
			client = createDavClient();
		}

		DavOperations operations = new DavOperations(client);
		operations.setEndpoint(this);
		return operations;
	}

	protected Sardine createDavClient() throws Exception {
		return SardineFactory.begin();
	}

	@Override
	public DavConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = new DavConfiguration();
		}
		return (DavConfiguration) configuration;
	}

	@Override
	public void setConfiguration(GenericFileConfiguration configuration) {
		setConfiguration((DavConfiguration) configuration);
	}

	public void setConfiguration(DavConfiguration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException("DavConfiguration expected");
		}
		this.configuration = configuration;
	}

	/**
	 * @return the davClient
	 */
	public Sardine getDavClient() {
		return davClient;
	}

	/**
	 * @param davClient
	 *            the davClient to set
	 */
	public void setDavClient(Sardine davClient) {
		this.davClient = davClient;
	}

}
