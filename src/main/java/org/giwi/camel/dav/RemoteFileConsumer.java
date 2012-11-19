/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.giwi.camel.dav;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConsumer;
import org.apache.camel.util.FileUtil;

/**
 * Base class for remote file consumers.
 */
public abstract class RemoteFileConsumer<T> extends GenericFileConsumer<T> {
	protected boolean loggedIn = true;

	public RemoteFileConsumer(RemoteFileEndpoint<T> endpoint, Processor processor, RemoteFileOperations<T> operations) {
		super(endpoint, processor, operations);
		setPollStrategy(new RemoteFilePollingConsumerPollStrategy());
	}

	@Override
	@SuppressWarnings("unchecked")
	public RemoteFileEndpoint<T> getEndpoint() {
		return (RemoteFileEndpoint<T>) super.getEndpoint();
	}

	protected RemoteFileOperations<T> getOperations() {
		return (RemoteFileOperations<T>) operations;
	}

	@Override
	protected boolean prePollCheck() throws Exception {
		// noop
		return true;
	}

	@Override
	protected void postPollCheck() {
		// noop
	}

	@Override
	protected void processExchange(Exchange exchange) {
		// mark the exchange to be processed synchronously as the dav client is not thread safe
		// and we must execute the callbacks in the same thread as this consumer
		exchange.setProperty(Exchange.UNIT_OF_WORK_PROCESS_SYNC, Boolean.TRUE);
		super.processExchange(exchange);
	}

	protected boolean isRetrieveFile() {
		return getEndpoint().isDownload();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
	}

	protected void disconnect() {
		// noop
	}

	protected void recoverableConnectIfNecessary() throws Exception {
		// noop
	}

	protected void connectIfNecessary() throws IOException {
		// noop
	}

	/**
	 * Returns human readable server information for logging purpose
	 */
	protected String remoteServer() {
		return ((RemoteFileEndpoint<?>) endpoint).remoteServerInformation();
	}

	@Override
	protected boolean isMatched(GenericFile<T> file, String doneFileName) {
		// dav specific as we need to cater for stepwise
		if (getEndpoint().getConfiguration().isStepwise()) {
			// stepwise enabled, so done file should always be without path
			doneFileName = FileUtil.stripPath(doneFileName);
		}

		return super.isMatched(file, doneFileName);
	}
}
