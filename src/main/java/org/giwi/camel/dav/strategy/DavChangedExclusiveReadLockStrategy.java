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
package org.giwi.camel.dav.strategy;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileEndpoint;
import org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy;
import org.apache.camel.component.file.GenericFileOperations;
import org.apache.camel.util.CamelLogger;
import org.apache.camel.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.DavResource;

/**
 * The Class DavChangedExclusiveReadLockStrategy.
 * 
 * @author Giwi Softwares
 */
public class DavChangedExclusiveReadLockStrategy implements GenericFileExclusiveReadLockStrategy<DavResource> {

	/** The Constant LOG. */
	private static final transient Logger LOG = LoggerFactory.getLogger(DavChangedExclusiveReadLockStrategy.class);

	/** The timeout. */
	private long timeout;

	/** The check interval. */
	private long checkInterval = 5000;

	/** The min length. */
	private long minLength = 1;

	/** The fast exists check. */
	private boolean fastExistsCheck;

	private LoggingLevel readLockLoggingLevel = LoggingLevel.WARN;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy# prepareOnStartup(org.apache.camel.component.file.GenericFileOperations,
	 * org.apache.camel.component.file.GenericFileEndpoint)
	 */
	@Override
	public void prepareOnStartup(GenericFileOperations<DavResource> tGenericFileOperations, GenericFileEndpoint<DavResource> tGenericFileEndpoint) throws Exception {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy# acquireExclusiveReadLock (org.apache.camel.component.file.GenericFileOperations,
	 * org.apache.camel.component.file.GenericFile, org.apache.camel.Exchange)
	 */
	@Override
	public boolean acquireExclusiveReadLock(GenericFileOperations<DavResource> operations, GenericFile<DavResource> file, Exchange exchange) throws Exception {
		boolean exclusive = false;
		if (LOG.isTraceEnabled()) {
			LOG.trace("Waiting for exclusive read lock to file: " + file);
		}

		long lastModified = Long.MIN_VALUE;
		long length = Long.MIN_VALUE;
		StopWatch watch = new StopWatch();

		while (!exclusive) {
			// timeout check
			if (timeout > 0) {
				long delta = watch.taken();
				if (delta > timeout) {
					CamelLogger.log(LOG, readLockLoggingLevel, "Cannot acquire read lock within " + timeout + " millis. Will skip the file: " + file);
					// we could not get the lock within the timeout period, so
					// return false
					return false;
				}
			}

			long newLastModified = 0;
			long newLength = 0;
			List<DavResource> files;
			if (fastExistsCheck) {
				// use the absolute file path to only pickup the file we want to
				// check, this avoids expensive
				// list operations if we have a lot of files in the directory
				if (LOG.isTraceEnabled()) {
					LOG.trace("Using fast exists to update file information for {}", file);
				}
				files = operations.listFiles(file.getAbsoluteFilePath());
			} else {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Using full directory listing to update file information for {}. Consider enabling fastExistsCheck option.", file);
				}
				// fast option not enabled, so list the directory and filter the
				// file name
				files = operations.listFiles(file.getParent());
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("List files {} found {} files", file.getAbsoluteFilePath(), files.size());
			}
			for (DavResource f : files) {
				if (f.getName().equals(file.getFileNameOnly())) {
					newLastModified = f.getModified().getTime();
					newLength = f.getContentLength();
				}
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Previous last modified: " + lastModified + ", new last modified: " + newLastModified);
				LOG.trace("Previous length: " + length + ", new length: " + newLength);
			}
			if (length >= minLength && newLastModified == lastModified && newLength == length) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Read lock acquired.");
				}
				exclusive = true;
			} else {
				// set new base file change information
				lastModified = newLastModified;
				length = newLength;

				boolean interrupted = sleep();
				if (interrupted) {
					// we were interrupted while sleeping, we are likely being
					// shutdown so return false
					return false;
				}
			}
		}

		return exclusive;
	}

	/**
	 * Sleep.
	 * 
	 * @return true, if successful
	 */
	private boolean sleep() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Exclusive read lock not granted. Sleeping for " + checkInterval + " millis.");
		}
		try {
			Thread.sleep(checkInterval);
			return false;
		} catch (InterruptedException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Sleep interrupted while waiting for exclusive read lock, so breaking out");
			}
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy# releaseExclusiveReadLock (org.apache.camel.component.file.GenericFileOperations,
	 * org.apache.camel.component.file.GenericFile, org.apache.camel.Exchange)
	 */
	@Override
	public void releaseExclusiveReadLock(GenericFileOperations<DavResource> tGenericFileOperations, GenericFile<DavResource> tGenericFile, Exchange exchange) throws Exception {
		// noop
	}

	/**
	 * Gets the timeout.
	 * 
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy# setTimeout(long)
	 */
	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets the check interval.
	 * 
	 * @return the check interval
	 */
	public long getCheckInterval() {
		return checkInterval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy# setCheckInterval(long)
	 */
	@Override
	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	/**
	 * Gets the min length.
	 * 
	 * @return the min length
	 */
	public long getMinLength() {
		return minLength;
	}

	/**
	 * Sets the min length.
	 * 
	 * @param minLength
	 *            the new min length
	 */
	public void setMinLength(long minLength) {
		this.minLength = minLength;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy#setReadLockLoggingLevel(org.apache.camel.LoggingLevel)
	 */
	@Override
	public void setReadLockLoggingLevel(LoggingLevel readLockLoggingLevel) {
		this.readLockLoggingLevel = readLockLoggingLevel;
	}
}