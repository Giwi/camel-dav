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

import org.apache.camel.Exchange;
import org.apache.camel.ServicePoolAware;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.component.file.GenericFileProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;

/**
 * Generic remote file producer for all the DAV variations.
 * 
 * @param <T>
 *            the generic type
 */
public class RemoteFileProducer<T> extends GenericFileProducer<T> implements
	ServicePoolAware {

    /** The logged in. */
    private boolean loggedIn;

    /**
     * Instantiates a new remote file producer.
     * 
     * @param endpoint
     *            the endpoint
     * @param operations
     *            the operations
     */
    protected RemoteFileProducer(RemoteFileEndpoint<T> endpoint,
	    RemoteFileOperations<T> operations) {
	super(endpoint, operations);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileProducer#getFileSeparator()
     */
    @Override
    public String getFileSeparator() {
	return "/";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileProducer#normalizePath(java
     * .lang.String)
     */
    @Override
    public String normalizePath(String name) {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileProducer#process(org.apache
     * .camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
	String target = createFileName(exchange);
	processExchange(exchange, target);
    }

    /**
     * Gets the operations.
     * 
     * @return the operations
     */
    protected RemoteFileOperations<T> getOperations() {
	return (RemoteFileOperations<T>) operations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.impl.DefaultProducer#getEndpoint()
     */
    @Override
    @SuppressWarnings("unchecked")
    public RemoteFileEndpoint<T> getEndpoint() {
	return (RemoteFileEndpoint<T>) super.getEndpoint();
    }

    /**
     * The file could not be written. We need to disconnect from the remote
     * server.
     * 
     * @param exchange
     *            the exchange
     * @param exception
     *            the exception
     * @throws Exception
     *             the exception
     */
    @Override
    public void handleFailedWrite(Exchange exchange, Exception exception)
	    throws Exception {
	loggedIn = false;
	if (isStopping() || isStopped()) {
	    // if we are stopping then ignore any exception during a poll
	    log.debug("Exception occurred during stopping: "
		    + exception.getMessage());
	} else {
	    log.warn("Writing file failed with: " + exception.getMessage());
	    try {
		disconnect();
	    } catch (Exception e) {
		// ignore exception
		log.debug("Ignored exception during disconnect: "
			+ e.getMessage());
	    }
	    // rethrow the original exception*/
	    throw exception;
	}
    }

    /**
     * Disconnect.
     * 
     * @throws GenericFileOperationFailedException
     *             the generic file operation failed exception
     */
    public void disconnect() throws GenericFileOperationFailedException {
	// noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileProducer#preWriteCheck()
     */
    @Override
    public void preWriteCheck() throws Exception {
	// noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileProducer#postWriteCheck()
     */
    @Override
    public void postWriteCheck() {
	try {
	    if (getEndpoint().isDisconnect()) {
		log.trace("postWriteCheck disconnect from: {}", getEndpoint());
		disconnect();
	    }
	} catch (GenericFileOperationFailedException e) {
	    // ignore just log a warning
	    log.warn("Exception occurred during disconnecting from: "
		    + getEndpoint() + " " + e.getMessage());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileProducer#doStart()
     */
    @Override
    protected void doStart() throws Exception {
	log.debug("Starting");
	// do not connect when component starts, just wait until we process as
	// we will
	// connect at that time if needed
	super.doStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileProducer#doStop()
     */
    @Override
    protected void doStop() throws Exception {
	try {
	    disconnect();
	} catch (Exception e) {
	    log.debug("Exception occurred during disconnecting from: "
		    + getEndpoint() + " " + e.getMessage());
	}
	super.doStop();
    }

    /**
     * Recoverable connect if necessary.
     * 
     * @throws Exception
     *             the exception
     */
    protected void recoverableConnectIfNecessary() throws Exception {
	try {
	    connectIfNecessary();
	} catch (Exception e) {
	    loggedIn = false;

	    // are we interrupted
	    InterruptedException ie = ObjectHelper.getException(
		    InterruptedException.class, e);
	    if (ie != null) {
		if (log.isDebugEnabled()) {
		    log.debug(
			    "Interrupted during connect to: " + getEndpoint(),
			    ie);
		}
		throw ie;
	    }

	    if (log.isDebugEnabled()) {
		log.debug("Could not connect to: " + getEndpoint()
			+ ". Will try to recover.", e);
	    }
	}

	// recover by re-creating operations which should most likely be able to
	// recover
	if (!loggedIn) {
	    log.debug(
		    "Trying to recover connection to: {} with a fresh client.",
		    getEndpoint());
	    setOperations(getEndpoint().createRemoteFileOperations());
	    connectIfNecessary();
	}
    }

    /**
     * Connect if necessary.
     * 
     * @throws GenericFileOperationFailedException
     *             the generic file operation failed exception
     */
    protected void connectIfNecessary()
	    throws GenericFileOperationFailedException {
	// noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.impl.DefaultProducer#isSingleton()
     */
    @Override
    public boolean isSingleton() {
	// this producer is stateful because the remote file operations is not
	// thread safe
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.impl.DefaultProducer#toString()
     */
    @Override
    public String toString() {
	return "RemoteFileProducer["
		+ URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
    }
}
