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

import java.util.List;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.URISupport;

import com.googlecode.sardine.DavResource;

/**
 * The Sardine consumer.
 * 
 * @author Giwi Softwares
 * 
 */
public class DavConsumer extends RemoteFileConsumer<DavResource> {
	protected String endpointPath;

	public DavConsumer(RemoteFileEndpoint<DavResource> endpoint, Processor processor, RemoteFileOperations<DavResource> fileOperations) {
		super(endpoint, processor, fileOperations);
		endpointPath = endpoint.getConfiguration().getRemoteServerInformation();
		if (log.isInfoEnabled()) {
			log.info("endpointPath : " + endpointPath);
		}
		if (endpoint.isAutoCreate()) {
			((DavOperations) operations).initComponent(endpoint.getConfiguration().getInitialDirectory());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.camel.component.file.GenericFileConsumer#pollDirectory(java.lang.String, java.util.List, int)
	 */
	@Override
	protected boolean pollDirectory(String fileName, List<GenericFile<DavResource>> fileList, int depth) {
		if (log.isDebugEnabled()) {
			log.debug("pollDirectory : " + fileName);
		}
		// strip trailing slash
		return doPollDirectory(FileUtil.stripTrailingSeparator(fileName), null, fileList, depth);
	}

	/**
	 * @param absolutePath
	 * @param dirName
	 * @param fileList
	 * @param depth
	 * @return
	 */
	protected boolean pollSubDirectory(String absolutePath, String dirName, List<GenericFile<DavResource>> fileList, int depth) {
		return doPollDirectory(absolutePath, dirName, fileList, depth);
	}

	/**
	 * @param absolutePath
	 * @param dirName
	 * @param fileList
	 * @param depth
	 * @return
	 */
	protected boolean doPollDirectory(String absolutePath, String dirName, List<GenericFile<DavResource>> fileList, int depth) {
		if (log.isDebugEnabled()) {
			log.debug("doPollDirectory from absolutePath: {}, dirName: {}", absolutePath, dirName);
		}
		depth++;
		// remove trailing /
		dirName = FileUtil.stripTrailingSeparator(dirName);
		String dir = absolutePath;
		log.debug("Polling directory: {} / {}", dir, dirName);
		List<DavResource> files;
		files = operations.listFiles(dir);

		if (files == null || files.isEmpty()) {
			// no files in this directory to poll
			if (log.isDebugEnabled()) {
				log.debug("No files found in directory: {}", dir);
			}
			return true;
		} else {
			// we found some files
			if (log.isDebugEnabled()) {
				log.debug("Found {} in directory: {}", files.size(), dir);
			}
		}

		for (DavResource file : files) {
			if (log.isInfoEnabled()) {
				log.info("found : " + file.getName());
			}
			// check if we can continue polling in files
			if (!canPollMoreFiles(fileList)) {
				return false;
			}
			if (!file.getName().equalsIgnoreCase(endpoint.getConfiguration().getDirectory()) && !file.getName().equalsIgnoreCase(dirName)) {
				if (file.isDirectory()) {
					RemoteFile<DavResource> remote = asRemoteFile(absolutePath, file);
					if (endpoint.isRecursive() && isValidFile(remote, true) && depth < endpoint.getMaxDepth()) {
						// recursive scan and add the sub files and folders
						String subDirectory = file.getName();
						String path = absolutePath + "/" + subDirectory;
						boolean canPollMore = pollSubDirectory(path, subDirectory, fileList, depth);
						if (!canPollMore) {
							return false;
						}
					}
				} else {
					RemoteFile<DavResource> remote = asRemoteFile(absolutePath, file);
					if (isValidFile(remote, false) && depth >= endpoint.getMinDepth()) {
						if (isInProgress(remote)) {
							if (log.isTraceEnabled()) {
								log.trace("Skipping as file is already in progress: {}", remote.getFileName());
							}
						} else if (remote != null) {
							// matched file so add
							fileList.add(remote);
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Fixes the path separator to be according to the protocol
	 */
	// protected String normalizePathToProtocol(String path) {
	// if (ObjectHelper.isEmpty(path)) {
	// return path;
	// }
	// path = path.replace('/', File.separatorChar);
	// path = path.replace('\\', File.separatorChar);
	// return path;
	// }

	/**
	 * @param absolutePath
	 * @param file
	 * @return
	 */
	private RemoteFile<DavResource> asRemoteFile(String absolutePath, DavResource file) {
		RemoteFile<DavResource> answer = new RemoteFile<DavResource>();
		answer.setEndpointPath(endpointPath);
		answer.setFile(file);
		answer.setFileNameOnly(file.getName());
		answer.setFileLength(file.getContentLength());

		answer.setDirectory(file.isDirectory());
		if (file.isDirectory()) {
			answer.setRelativeFilePath(getRelativePath(file));
			answer.setFileName(getRelativePath(file));
		} else {
			answer.setRelativeFilePath(getRelativePath(file) + file.getName());
			answer.setFileName(getRelativePath(file) + file.getName());
		}
		System.out.println(file.getPath());
		answer.setAbsoluteFilePath(FileUtil.stripLeadingSeparator(file.getPath()));
		answer.setHostname(((RemoteFileConfiguration) endpoint.getConfiguration()).getHost());
		if (file.getModified() != null) {
			answer.setLastModified(file.getModified().getTime());
		}
		if (log.isDebugEnabled()) {
			log.debug("found : " + answer);
		}
		return answer;
	}

	/**
	 * @param file
	 * @return
	 */
	private String getRelativePath(DavResource file) {
		String relativefileName = FileUtil.stripLeadingSeparator(file.getPath().replaceFirst(((RemoteFileConfiguration) endpoint.getConfiguration()).getInitialDirectory(), ""));
		int lastSep = relativefileName.lastIndexOf('/');
		if (file.isDirectory()) {
			return relativefileName;
		} else if (lastSep == -1) {
			return "";
		}
		return relativefileName.substring(0, lastSep + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.camel.impl.DefaultConsumer#toString()
	 */
	@Override
	public String toString() {
		return "DavConsumer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
	}
}
