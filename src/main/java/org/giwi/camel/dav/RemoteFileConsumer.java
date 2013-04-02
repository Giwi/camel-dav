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

import java.io.IOException;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFileConsumer;

/**
 * Base class for remote file consumers.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class RemoteFileConsumer<T> extends GenericFileConsumer<T> {

    /** The logged in. */
    protected boolean loggedIn = true;

    /**
     * Instantiates a new remote file consumer.
     * 
     * @param endpoint
     *            the endpoint
     * @param processor
     *            the processor
     * @param operations
     *            the operations
     */
    public RemoteFileConsumer(RemoteFileEndpoint<T> endpoint,
	    Processor processor, RemoteFileOperations<T> operations) {
	super(endpoint, processor, operations);
	setPollStrategy(new RemoteFilePollingConsumerPollStrategy());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.impl.DefaultConsumer#getEndpoint()
     */
    @Override
    @SuppressWarnings("unchecked")
    public RemoteFileEndpoint<T> getEndpoint() {
	return (RemoteFileEndpoint<T>) super.getEndpoint();
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
     * @see org.apache.camel.component.file.GenericFileConsumer#prePollCheck()
     */
    @Override
    protected boolean prePollCheck() throws Exception {
	// noop
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFileConsumer#postPollCheck()
     */
    @Override
    protected void postPollCheck() {
	// noop
    }

    // @Override
    // protected void processExchange(Exchange exchange) {
    // // mark the exchange to be processed synchronously as the dav client is
    // not thread safe
    // // and we must execute the callbacks in the same thread as this consumer
    // exchange.setProperty(Exchange.UNIT_OF_WORK_PROCESS_SYNC, Boolean.TRUE);
    // super.processExchange(exchange);
    // }

    /**
     * Checks if is retrieve file.
     * 
     * @return true, if is retrieve file
     */
    protected boolean isRetrieveFile() {
	return getEndpoint().getConfiguration().isDownload();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.impl.ScheduledPollConsumer#doStop()
     */
    @Override
    protected void doStop() throws Exception {
	super.doStop();
    }

    /**
     * Disconnect.
     */
    protected void disconnect() {
	// noop
    }

    /**
     * Recoverable connect if necessary.
     * 
     * @throws Exception
     *             the exception
     */
    protected void recoverableConnectIfNecessary() throws Exception {
	// noop
    }

    /**
     * Connect if necessary.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void connectIfNecessary() throws IOException {
	// noop
    }

    /**
     * Returns human readable server information for logging purpose.
     * 
     * @return the string
     */
    protected String remoteServer() {
	return ((RemoteFileEndpoint<?>) endpoint).remoteServerInformation();
    }

}
