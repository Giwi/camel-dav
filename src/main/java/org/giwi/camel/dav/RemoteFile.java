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

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileMessage;

/**
 * Represents a remote file of some sort of backing object.
 * 
 * @param <T>
 *            the type of file that these remote endpoints provide
 */
public class RemoteFile<T> extends GenericFile<T> implements Cloneable {

    /** The hostname. */
    private String hostname;

    /**
     * Populates the {@link GenericFileMessage} relevant headers.
     * 
     * @param message
     *            the message to populate with headers
     */
    @Override
    public void populateHeaders(GenericFileMessage<T> message) {
	if (message != null) {
	    super.populateHeaders(message);
	    message.setHeader("CamelFileHost", getHostname());
	}
    }

    /**
     * Gets the hostname.
     * 
     * @return the hostname
     */
    public String getHostname() {
	return hostname;
    }

    /**
     * Sets the hostname.
     * 
     * @param hostname
     *            the new hostname
     */
    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.component.file.GenericFile#getFileSeparator()
     */
    @Override
    public char getFileSeparator() {
	// always use / as separator for DAV
	return '/';
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFile#isAbsolute(java.lang.String)
     */
    @Override
    protected boolean isAbsolute(String name) {
	return name.startsWith("" + getFileSeparator());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFile#normalizePath(java.lang.String
     * )
     */
    @Override
    protected String normalizePath(String name) {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFile#copyFromPopulateAdditional
     * (org.apache.camel.component.file.GenericFile,
     * org.apache.camel.component.file.GenericFile)
     */
    @Override
    public void copyFromPopulateAdditional(GenericFile<T> source,
	    GenericFile<T> result) {
	RemoteFile<?> remoteSource = (RemoteFile<?>) source;
	RemoteFile<?> remoteResult = (RemoteFile<?>) result;

	remoteResult.setHostname(remoteSource.getHostname());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("RemoteFile [hostname=").append(hostname)
		.append(", getRelativeFilePath()=")
		.append(getRelativeFilePath()).append(", getFileName()=")
		.append(getFileName()).append(", getAbsoluteFilePath()=")
		.append(getAbsoluteFilePath()).append(", getFileNameOnly()=")
		.append(getFileNameOnly()).append("]");
	return builder.toString();
    }

}
