package org.giwi.camel.dav;

import java.util.List;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.DavResource;

/**
 * The Sardine consumer.
 */
public class DavConsumer extends RemoteFileConsumer<DavResource> {
	private static final transient Logger LOG = LoggerFactory.getLogger(DavConsumer.class);
	protected String endpointPath;

	public DavConsumer(RemoteFileEndpoint<DavResource> endpoint, Processor processor, RemoteFileOperations<DavResource> fileOperations) {
		super(endpoint, processor, fileOperations);
		endpointPath = endpoint.getConfiguration().remoteServerInformation();
		LOG.info("endpointPath : " + endpointPath);
		if (endpoint.isAutoCreate()) {
			operations.buildDirectory(endpoint.getConfiguration().getInitialDirectory(), true);
		}
	}

	@Override
	protected boolean pollDirectory(String fileName, List<GenericFile<DavResource>> fileList, int depth) {
		String currentDir = null;
		LOG.debug("pollDirectory : " + fileName);
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
			log.info("found : " + file.getName());
			// check if we can continue polling in files
			if (!canPollMoreFiles(fileList)) {
				return false;
			}
			// TODO gérer la récursivité
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

	private RemoteFile<DavResource> asRemoteFile(String absolutePath, DavResource file) {
		RemoteFile<DavResource> answer = new RemoteFile<DavResource>();

		answer.setEndpointPath(endpointPath);
		answer.setFile(file);
		answer.setFileNameOnly(file.getName());
		answer.setFileLength(file.getContentLength());
		answer.setDirectory(file.isDirectory());
		answer.setFileName(file.getName());
		answer.setRelativeFilePath(((RemoteFileConfiguration) endpoint.getConfiguration()).getDirectory() + "/" + file.getName());
		answer.setAbsoluteFilePath(((RemoteFileConfiguration) endpoint.getConfiguration()).remoteServerInformation() + "/" + answer.getRelativeFilePath());
		System.out.println(answer);

		if (file.getCreation() != null) {
			answer.setLastModified(file.getCreation().getTime());
		}
		answer.setHostname(((RemoteFileConfiguration) endpoint.getConfiguration()).getHost());

		// absolute or relative path
		boolean absolute = FileUtil.hasLeadingSeparator(absolutePath);
		answer.setAbsolute(true);

		// create a pseudo absolute name
		String dir = FileUtil.stripTrailingSeparator(absolutePath);
		String absoluteFileName = FileUtil.stripLeadingSeparator(dir.replaceAll(endpoint.getConfiguration().getDirectory(), "") + "/" + file.getName());
		answer.setAbsoluteFilePath(absoluteFileName); // file.getName());

		// if (dir.equals(file.getName()) || "camel".equalsIgnoreCase(file.getName())) {
		//
		// return null;
		// absoluteFileName = FileUtil.stripLeadingSeparator(file.getName());
		// answer.setAbsoluteFilePath(((RemoteFileConfiguration) endpoint.getConfiguration()).remoteServerInformation() + file.getName());
		// // if absolute start with a leading separator otherwise let it be relative
		// if (absolute) {
		// absoluteFileName = "/" + absoluteFileName;
		// }
		// }

		// the relative filename, skip the leading endpoint configured path
		// String relativePath = ObjectHelper.after(absoluteFileName, endpointPath);
		// if (relativePath == null) {
		// relativePath = ".";
		// }

		// skip leading /
		// relativePath = FileUtil.stripLeadingSeparator(relativePath);
		answer.setRelativeFilePath(file.getName());

		// the file name should be the relative path
		answer.setFileName(file.getName());

		return answer;
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
