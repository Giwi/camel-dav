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

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileEndpoint;
import org.apache.camel.component.file.GenericFileExist;
import org.apache.camel.component.file.GenericFileProducer;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.util.ObjectHelper;

/**
 * The Class RemoteFileEndpoint.
 * 
 * @param <T>
 *            the generic type
 * @author Giwi Softwares
 */
public abstract class RemoteFileEndpoint<T> extends GenericFileEndpoint<T> {

    /** The maximum reconnect attempts. */
    private int maximumReconnectAttempts = 3;

    /** The reconnect delay. */
    private long reconnectDelay = 1000;

    /** The disconnect. */
    private boolean disconnect;

    /** The fast exists check. */
    private boolean fastExistsCheck;

    /**
     * Instantiates a new remote file endpoint.
     */
    public RemoteFileEndpoint() {
	// no args constructor for spring bean endpoint configuration
	// for dav we need to use higher interval/checkout that for files
	setReadLockTimeout(20000);
	setReadLockCheckInterval(5000);
    }

    /**
     * Instantiates a new remote file endpoint.
     * 
     * @param uri
     *            the uri
     * @param component
     *            the component
     * @param configuration
     *            the configuration
     */
    public RemoteFileEndpoint(String uri, RemoteFileComponent<T> component,
	    RemoteFileConfiguration configuration) {
	super(uri, component);
	this.configuration = configuration;
	// for dav we need to use higher interval/checkout that for files
	setReadLockTimeout(20000);
	setReadLockCheckInterval(5000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#getConfiguration()
     */
    @Override
    public RemoteFileConfiguration getConfiguration() {
	return (RemoteFileConfiguration) configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#createExchange(org
     * .apache.camel.component.file.GenericFile)
     */
    @Override
    public Exchange createExchange(GenericFile<T> file) {
	Exchange answer = new DefaultExchange(this);
	if (file != null) {
	    file.bindToExchange(answer);
	}
	return answer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileEndpoint#createProducer()
     */
    @Override
    public GenericFileProducer<T> createProducer() throws Exception {
	afterPropertiesSet();
	// ensure fileExist and moveExisting is configured correctly if in use
	if (getFileExist() == GenericFileExist.Move
		&& getMoveExisting() == null) {
	    throw new IllegalArgumentException(
		    "You must configure moveExisting option when fileExist=Move");
	} else if (getMoveExisting() != null
		&& getFileExist() != GenericFileExist.Move) {
	    throw new IllegalArgumentException(
		    "You must configure fileExist=Move when moveExisting has been set");
	}
	return buildProducer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#createConsumer(org
     * .apache.camel.Processor)
     */
    @Override
    public RemoteFileConsumer<T> createConsumer(Processor processor)
	    throws Exception {
	afterPropertiesSet();
	RemoteFileConsumer<T> consumer = buildConsumer(processor);

	if (isDelete() && getMove() != null) {
	    throw new IllegalArgumentException(
		    "You cannot both set delete=true and move options");
	}
	// if noop=true then idempotent should also be configured
	if (isNoop() && !isIdempotent()) {
	    log.info("Endpoint is configured with noop=true so forcing endpoint to be idempotent as well");
	    setIdempotent(true);
	}

	// if idempotent and no repository set then create a default one
	if (isIdempotent() && idempotentRepository == null) {
	    log.info("Using default memory based idempotent repository with cache max size: "
		    + DEFAULT_IDEMPOTENT_CACHE_SIZE);
	    idempotentRepository = MemoryIdempotentRepository
		    .memoryIdempotentRepository(DEFAULT_IDEMPOTENT_CACHE_SIZE);
	}

	// set max messages per poll
	consumer.setMaxMessagesPerPoll(getMaxMessagesPerPoll());
	consumer.setEagerLimitMaxMessagesPerPoll(isEagerMaxMessagesPerPoll());

	configureConsumer(consumer);
	return consumer;
    }

    /**
     * Validates this endpoint if its configured properly.
     * 
     * @throws Exception
     *             is thrown if endpoint is invalid configured for its mandatory
     *             options
     */
    protected void afterPropertiesSet() throws Exception {
	RemoteFileConfiguration config = getConfiguration();
	ObjectHelper.notEmpty(config.getHost(), "host");
	// ObjectHelper.notEmpty(config.getProtocol(), "protocol");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileEndpoint#getParamsAsMap()
     */
    @Override
    protected Map<String, Object> getParamsAsMap() {
	Map<String, Object> map = super.getParamsAsMap();
	map.put("fastExistsCheck", fastExistsCheck);
	return map;
    }

    /**
     * Remote File Endpoints, impl this method to create a custom consumer
     * specific to their "protocol" etc.
     * 
     * @param processor
     *            the processor
     * @return the created consumer
     */
    protected abstract RemoteFileConsumer<T> buildConsumer(Processor processor);

    /**
     * Remote File Endpoints, impl this method to create a custom producer
     * specific to their "protocol" etc.
     * 
     * @return the created producer
     */
    protected abstract GenericFileProducer<T> buildProducer();

    /**
     * Creates the operations to be used by the consumer or producer.
     * 
     * @return a new created operations
     * @throws Exception
     *             is thrown if error creating operations.
     */
    public abstract RemoteFileOperations<T> createRemoteFileOperations()
	    throws Exception;

    /**
     * Returns human readable server information for logging purpose.
     * 
     * @return the string
     */
    public String remoteServerInformation() {
	return ((RemoteFileConfiguration) configuration)
		.getRemoteServerInformation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#getFileSeparator()
     */
    @Override
    public char getFileSeparator() {
	return '/';
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileEndpoint#isAbsolute(java.lang
     * .String)
     */
    @Override
    public boolean isAbsolute(String name) {
	return name.startsWith("/");
    }

    /**
     * Gets the maximum reconnect attempts.
     * 
     * @return the maximum reconnect attempts
     */
    public int getMaximumReconnectAttempts() {
	return maximumReconnectAttempts;
    }

    /**
     * Sets the maximum reconnect attempts.
     * 
     * @param maximumReconnectAttempts
     *            the new maximum reconnect attempts
     */
    public void setMaximumReconnectAttempts(int maximumReconnectAttempts) {
	this.maximumReconnectAttempts = maximumReconnectAttempts;
    }

    /**
     * Gets the reconnect delay.
     * 
     * @return the reconnect delay
     */
    public long getReconnectDelay() {
	return reconnectDelay;
    }

    /**
     * Sets the reconnect delay.
     * 
     * @param reconnectDelay
     *            the new reconnect delay
     */
    public void setReconnectDelay(long reconnectDelay) {
	this.reconnectDelay = reconnectDelay;
    }

    /**
     * Checks if is disconnect.
     * 
     * @return true, if is disconnect
     */
    public boolean isDisconnect() {
	return disconnect;
    }

    /**
     * Sets the disconnect.
     * 
     * @param disconnect
     *            the new disconnect
     */
    public void setDisconnect(boolean disconnect) {
	this.disconnect = disconnect;
    }

    /**
     * Checks if is fast exists check.
     * 
     * @return true, if is fast exists check
     */
    public boolean isFastExistsCheck() {
	return fastExistsCheck;
    }

    /**
     * Sets the fast exists check.
     * 
     * @param fastExistsCheck
     *            the new fast exists check
     */
    public void setFastExistsCheck(boolean fastExistsCheck) {
	this.fastExistsCheck = fastExistsCheck;
    }

}
