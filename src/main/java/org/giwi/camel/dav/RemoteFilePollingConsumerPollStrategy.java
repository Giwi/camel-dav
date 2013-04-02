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

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultPollingConsumerPollStrategy;

/**
 * Remote file consumer polling strategy that attempts to help recovering from
 * lost connections.
 * 
 * @version
 */
public class RemoteFilePollingConsumerPollStrategy extends
	DefaultPollingConsumerPollStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.impl.DefaultPollingConsumerPollStrategy#rollback(org
     * .apache.camel.Consumer, org.apache.camel.Endpoint, int,
     * java.lang.Exception)
     */
    @Override
    public boolean rollback(Consumer consumer, Endpoint endpoint,
	    int retryCounter, Exception e) throws Exception {
	RemoteFileConsumer<?> rfc = (RemoteFileConsumer<?>) consumer;

	// only try to recover if we are allowed to run
	if (((RemoteFileConsumer<?>) consumer).isRunAllowed()) {
	    // disconnect from the server to force it to re login at next poll
	    // to recover
	    log.warn("Trying to recover by disconnecting from remote server forcing a re-connect at next poll: "
		    + rfc.remoteServer());
	    try {
		rfc.disconnect();
	    } catch (Throwable t) {
		// ignore the exception
		log.debug(
			"Error occurred during disconnect from: "
				+ rfc.remoteServer()
				+ ". This exception will be ignored.", t);
	    }
	}

	return super.rollback(consumer, endpoint, retryCounter, e);
    }

}
