package org.giwi.camel.dav;

import java.util.List;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;

import com.googlecode.sardine.DavResource;

/**
 * The Sardine consumer.
 */
public class DavConsumer extends RemoteFileConsumer<DavResource> {
	protected String endpointPath;

	public DavConsumer(RemoteFileEndpoint<DavResource> endpoint, Processor processor, RemoteFileOperations<DavResource> fileOperations) {
		super(endpoint, processor, fileOperations);
		endpointPath = endpoint.getConfiguration().getRemoteServerInformation();
		log.info("endpointPath : " + endpointPath);
		if (endpoint.isAutoCreate()) {
			operations.buildDirectory(endpoint.getConfiguration().getInitialDirectory(), true);
		}
	}

	@Override
	protected boolean pollDirectory(String fileName, List<GenericFile<DavResource>> fileList, int depth) {
		String currentDir = null;
		log.debug("pollDirectory : " + fileName);
		if (isStepwise()) {
			// must remember current dir so we stay in that directory after the poll
			currentDir = operations.getCurrentDirectory();
		}

		// strip trailing slash
		fileName = FileUtil.stripTrailingSeparator(fileName);

		boolean answer = doPollDirectory(fileName, null, fileList, depth);
		if (currentDir != null) {
			operations.changeCurrentDirectory(currentDir);
		}

		return answer;
	}

	protected boolean pollSubDirectory(String absolutePath, String dirName, List<GenericFile<DavResource>> fileList, int depth) {
		boolean answer = doPollDirectory(absolutePath, dirName, fileList, depth);
		// change back to parent directory when finished polling sub directory
		if (isStepwise()) {
			operations.changeToParentDirectory();
		}
		return answer;
	}

	protected boolean doPollDirectory(String absolutePath, String dirName, List<GenericFile<DavResource>> fileList, int depth) {
		log.debug("doPollDirectory from absolutePath: {}, dirName: {}", absolutePath, dirName);

		depth++;

		// remove trailing /
		dirName = FileUtil.stripTrailingSeparator(dirName);

		// compute dir depending on stepwise is enabled or not
		String dir;
		if (isStepwise()) {
			dir = ObjectHelper.isNotEmpty(dirName) ? dirName : absolutePath;
			operations.changeCurrentDirectory(dir);
		} else {
			dir = absolutePath;
		}

		log.trace("Polling directory: {}", dir);
		List<DavResource> files;
		if (isStepwise()) {
			files = operations.listFiles();
		} else {
			files = operations.listFiles(dir);
		}

		if (files == null || files.isEmpty()) {
			// no files in this directory to poll
			log.trace("No files found in directory: {}", dir);
			return true;
		} else {
			// we found some files
			log.trace("Found {} in directory: {}", files.size(), dir);
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
							log.trace("Skipping as file is already in progress: {}", remote.getFileName());
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

	private RemoteFile<DavResource> asRemoteFile(String absolutePath, DavResource file) {
		RemoteFile<DavResource> answer = new RemoteFile<DavResource>();
		answer.setEndpointPath(endpointPath);
		answer.setFile(file);
		answer.setFileNameOnly(file.getName());
		answer.setFileLength(file.getContentLength());
		answer.setDirectory(file.isDirectory());
		answer.setRelativeFilePath(getRelativePath(file));
		answer.setFileName(answer.getRelativeFilePath() + file.getName());
		// answer.setAbsoluteFilePath(((RemoteFileConfiguration) endpoint.getConfiguration()).getRemoteServerInformation() + "/" + FileUtil.stripLeadingSeparator(answer.getFileName()));
		answer.setAbsoluteFilePath(answer.getRelativeFilePath() + file.getName());
		if (file.getCreation() != null) {
			answer.setLastModified(file.getCreation().getTime());
		}
		// answer.setHostname(((RemoteFileConfiguration) endpoint.getConfiguration()).getHost());
		answer.setHostname("");
		if (log.isDebugEnabled()) {
			log.debug("found : " + answer);
		}
		return answer;
	}

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

	private boolean isStepwise() {
		RemoteFileConfiguration config = (RemoteFileConfiguration) endpoint.getConfiguration();
		return false; // config.isStepwise();
	}

	@Override
	public String toString() {
		return "DavConsumer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
	}
}
